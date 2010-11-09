package org.fitnesse.widget;

import org.apache.commons.io.FileUtils;

import java.io.File;

import static org.apache.commons.lang.StringUtils.substringBefore;

/**
 * TestUtil class for the SlimFixtureApi project.
 * @author mdasberg
 */
public final class SlimFixtureApiTestUtil {

    /**
     * Gets the pom file.
     * @return pomFile The pom file.
     */
    public static File getPomFile() {
        return new File(substringBefore(SlimFixtureApiTestUtil.class.getResource(SlimFixtureApiTestUtil.class.getSimpleName() + ".class").getPath(), "target") + "pom.xml");
    }

    /**
     * Gets the property file.
     * @return propertyFile The property file.
     */
    public static File getPropertyFile(boolean excludes) {
        return FileUtils.toFile(new SlimFixtureApiTestUtil().getClass().getResource("/slimfixtureapiwith" + (!excludes ? "out" : "") + "excludes.properties"));
    }


}
