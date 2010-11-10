package org.fitnesse.widget.maven;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.embedder.*;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.reactor.MavenExecutionException;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassRealmAdapter;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DuplicateRealmException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static org.apache.commons.lang.StringUtils.*;
import static org.apache.maven.embedder.MavenEmbedder.validateConfiguration;

/**
 * MavenHelper is a singleton that takes care of reading in the pom.xml and create a classloader
 * with all dependencies and classes which are available.
 *
 * @author mdasberg
 */
public class MavenHelper {
    private volatile static MavenHelper helper;

    private File pom;
    private List<String> excludes;
    private Configuration configuration;
    private ConfigurationValidationResult validationResult;
    private ClassRealm fitnesseRealm;
    private Date instanceDate;

    /**
     * Gets the instance.
     *
     * @return instance The instance.
     */
    public static MavenHelper instance(File pom, File properties) {
        if (helper == null) {
            synchronized (MavenHelper.class) {
                if (helper == null) {
                    helper = new MavenHelper(pom, properties);
                }
            }
        }
        return helper;
    }

    /**
     * Gets the instance.
     *
     * @return instance The instance.
     */
    public static MavenHelper newInstance(File pom, File properties) {
        helper = new MavenHelper(pom, properties);
        return helper;
    }

    /**
     * Constructor.
     *
     * @param pom The pom file.
     */
    private MavenHelper(File pom, File properties) {
        validateFile(pom, "Pom.xml");
        validateFile(properties, "Property file");
        this.pom = pom;
        Properties props = new Properties();
        try {
            props.load(new FileReader(properties));
            if (props.get("fixtureApiExcludes") != null) {
                excludes = Arrays.asList(split((String) props.get("fixtureApiExcludes"), ","));
            } else {
                excludes = new ArrayList<String>();
            }
        } catch (IOException e) {
            excludes = new ArrayList<String>();
        }
        configuration = getConfiguration();
        fitnesseRealm = getClassRealm();
        instanceDate = new Date();
    }

    /**
     * Validate file.
     *
     * @param file     The file.
     * @param fileName The filename.
     */
    private void validateFile(File file, String fileName) {
        if (file == null) {
            throw new FixtureApiPluginException(fileName + " not specified.");
        } else if (!file.exists()) {
            throw new FixtureApiPluginException(fileName + " not found at location: " + file.getPath());
        } else if (!file.isFile()) {
            throw new FixtureApiPluginException(fileName + " is not a file: " + file.getPath());
        }
    }

    /**
     * Gets the maven configuration.
     *
     * @return configuration The configuration.
     */
    private Configuration getConfiguration() {
        configuration = new DefaultConfiguration().setClassLoader(Thread.currentThread().getContextClassLoader()).setMavenEmbedderLogger(new MavenEmbedderConsoleLogger());
        if (MavenEmbedder.DEFAULT_USER_SETTINGS_FILE != null && MavenEmbedder.DEFAULT_USER_SETTINGS_FILE.exists()) {
            configuration.setUserSettingsFile(MavenEmbedder.DEFAULT_USER_SETTINGS_FILE);
        }
        if (MavenEmbedder.DEFAULT_GLOBAL_SETTINGS_FILE != null && MavenEmbedder.DEFAULT_GLOBAL_SETTINGS_FILE.exists()) {
            configuration.setGlobalSettingsFile(MavenEmbedder.DEFAULT_GLOBAL_SETTINGS_FILE);
        }

        String localRepositoryLocation = null;
        validationResult = validateConfiguration(configuration);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("Pom.xml is not valid.");
        } else {
            if (validationResult.getUserSettings() != null) {
                localRepositoryLocation = validationResult.getUserSettings().getLocalRepository();
            } else if (validationResult.getGlobalSettings() != null) {
                localRepositoryLocation = validationResult.getGlobalSettings().getLocalRepository();
            }
            configuration.setLocalRepository(localRepositoryLocation != null ? new File(localRepositoryLocation) : MavenEmbedder.DEFAULT_USER_SETTINGS_FILE);
        }
        return configuration;
    }

    /**
     * Gets the newly created ClassRealm for fitnesse.
     *
     * @return classRealm The ClassRealm.
     */
    private ClassRealm getClassRealm() {
        try {
            MavenExecutionRequest request = new DefaultMavenExecutionRequest().setBaseDirectory(pom.getParentFile()).setPomFile(pom.getName()).setPom(pom);
            Collection<String> classpathElements = getClasspathElements(configuration, request);

            ClassWorld world = new ClassWorld();
            fitnesseRealm = world.newRealm("fitnesse");
            for (String classpath : classpathElements) {
                File c = new File(classpath);
                if (c.exists()) {
                    fitnesseRealm.addConstituent(c.toURI().toURL());
                }
            }
        } catch (MalformedURLException e) {
            throw new FixtureApiPluginException("Unable to create classpath URL.", e);
        } catch (DuplicateRealmException e) {
            throw new FixtureApiPluginException("Realm already exists.", e);
        }
        return fitnesseRealm;
    }

    /**
     * Gets all classpath elements.
     *
     * @param configuration The maven configuration.
     * @param request       The reuest.
     * @return classpathElements The classpathElements.
     */
    private Collection<String> getClasspathElements(Configuration configuration, MavenExecutionRequest request) {
        Set<String> classpathElements = new HashSet<String>() {
            @Override
            public boolean add(String s) {
                if (s.contains("${project.basedir}" + File.separatorChar)) {
                    s = s.replace("${project.basedir}" + File.separatorChar, getBaseDir());
                    s = s + File.separatorChar;
                }
                return super.add(s);
            }
        };
        try {
            MavenEmbedder embedder = new MavenEmbedder(configuration);
            MavenExecutionResult executionResult = embedder.readProjectWithDependencies(request);
            MavenProject project = executionResult.getProject();
            if (project == null) {
                project = embedder.readProject(pom);
            }
            classpathElements.addAll(project.getRuntimeClasspathElements());
            classpathElements.addAll(project.getTestClasspathElements());
            classpathElements.addAll(project.getCompileClasspathElements());
        } catch (MavenEmbedderException e) {
            throw new FixtureApiPluginException("Unable to determine classpaths from pom.", e);
        } catch (DependencyResolutionRequiredException e) {
            throw new FixtureApiPluginException("Unable to determine classpaths elements from pom.", e);
        } catch (MavenExecutionException e) {
            throw new FixtureApiPluginException("Unable to execute from pom.", e);
        } catch (ProjectBuildingException e) {
            throw new FixtureApiPluginException("Unable to build project from pom.", e);
        }
        return classpathElements;
    }

    private boolean isExcluded(String fixture) {
        for (String exclude : excludes) {
            if (fixture.startsWith(exclude)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the fixtures from the classpath.
     *
     * @return fixtures The fixtures.
     */
    public Collection<String> getFixtures() {
        try {
            Field field = ClassRealmAdapter.class.getDeclaredField("realm");
            field.setAccessible(true);
            return getFixtures(((org.codehaus.plexus.classworlds.realm.ClassRealm) field.get(fitnesseRealm)).getURLs());
        } catch (NoSuchFieldException e) {
            throw new FixtureApiPluginException("Could not find realm.");
        } catch (IllegalAccessException e) {
            throw new FixtureApiPluginException("Could not access realm.");
        }
    }

    /**
     * Gets the date that the classpath was constructed.
     *
     * @return date The date.
     */
    public Date getInstanceDate() {
        return instanceDate;
    }

    /**
     * Gets the class from the Classrealm.
     *
     * @param className The classname.
     * @return clazz The Class.
     */
    public Class getClass(String className) {
        try {
            return fitnesseRealm.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new FixtureApiPluginException("Could not find class for name: " + className);
        }
    }

    /**
     * Gets the base directory.
     *
     * @return baseDirectory The base directory.
     */
    private String getBaseDir() {
        return pom.getAbsolutePath().substring(0, pom.getAbsolutePath().lastIndexOf(File.separator) + 1);
    }


    /**
     * Gets the Fixtures from the classpath.
     *
     * @param urls The urls.
     * @return fixtures The fixtures.
     */
    private Collection<String> getFixtures(URL... urls) {
        Set<String> fixtures = new HashSet<String>();
        for (URL url : urls) {
            if (url.getFile().endsWith("jar")) {
                fixtures.addAll(getFixturesFromJarFile(url));
            } else if (url.getProtocol().equals("file")) {
                File directory = new File(url.getPath());
                fixtures.addAll(getFixturesFromDirectory(directory, directory.getPath() + File.separator));
            }

        }
        return fixtures;
    }

    /**
     * Gets all the fixtures from the given URL.
     *
     * @param url The URL.
     * @return fixtures The fixtures.
     */
    private List<String> getFixturesFromJarFile(URL url) {
        List<String> fixtures = new ArrayList<String>();
        try {
            JarInputStream jar = new JarInputStream(new FileInputStream(new File(url.getPath())));
            JarEntry entry = jar.getNextJarEntry();
            while (entry != null) {
                if (!entry.isDirectory() && isFixture(entry.getName())) {
                    String fqn = fromFileToClassName(entry.getName());
                    if (!isExcluded(fqn)) {
                        fixtures.add(fqn);
                    }
                }
                entry = jar.getNextJarEntry();
            }
            jar.close();
        } catch (IOException e) {
            throw new FixtureApiPluginException("Could not open jar file.", e);
        }
        return fixtures;
    }

    /**
     * Gets the Classes from the given directory.
     *
     * @param directory The root directory.
     * @param basePath  The base path.
     * @return fixtures The fixtures.
     */
    private List<String> getFixturesFromDirectory(File directory, String basePath) {
        List<String> fixtures = new ArrayList<String>();
        if (directory.exists()) {
            for (File entry : directory.listFiles()) {
                String className = substringAfter(entry.getPath(), basePath);

                if (entry.isFile() && isFixture(className)) {
                    String fqn = fromFileToClassName(className);
                    if (!isExcluded(fqn)) {
                        fixtures.add(fqn);
                    }
                } else if (entry.isDirectory()) {
                    fixtures.addAll(getFixturesFromDirectory(entry, basePath));
                }
            }

        }
        return fixtures;
    }

    /**
     * Indicates that the current class is a fixture.
     *
     * @param className The name of the class.
     * @return <code>true</code> when the class is a fixture, otherwise <code>false</code>.
     */
    private boolean isFixture(String className) {
        return endsWithIgnoreCase(className, "fixture.class");
    }


    /**
     * Converts the name of the file to a classname.
     *
     * @param fileName The filename.
     * @return className The classname.
     */
    private String fromFileToClassName(final String fileName) {
        return fileName.substring(0, fileName.length() - 6).replaceAll("/|\\\\", "\\.");
    }

}