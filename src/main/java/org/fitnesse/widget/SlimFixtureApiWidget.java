package org.fitnesse.widget;

import org.fitnesse.widget.maven.FixtureApiPluginException;
import org.fitnesse.widget.maven.MavenHelper;
import org.fitnesse.widget.parser.FixtureParser;
import fitnesse.wikitext.widgets.ParentWidget;
import fitnesse.wikitext.widgets.WidgetWithTextArgument;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scans a project for fixture sources based upon the given pom.xml file and
 * renders the fixture's API in html as a slim table. Useful for testers who are searching for
 * fixture code within a project.
 * @author mdasberg
 */
public class SlimFixtureApiWidget extends ParentWidget implements WidgetWithTextArgument {
    public static final String FIXTURE_API = "fixtureApi";
    public static final String REGEXP = "^!" + FIXTURE_API + "\\s([^\\s\\r\\n]*\\bpom.xml\\b)(\\s([^\\s\\r\\n]*\\b.properties\\b))?(\\s(\\brefresh\\b))?";
    private static final Pattern POM_PATTERN = Pattern.compile(REGEXP);
    private File pom;
    private File properties;
    private boolean refresh = false;

    /**
     * Constructor.
     * @param parent The parent widget.
     * @param text The text.
     * @throws Exception The Exception.
     */
    public SlimFixtureApiWidget(ParentWidget parent, String text) {
        super(parent);
        Matcher matcher = POM_PATTERN.matcher(text);
        pom = null;
        properties = null;
        if (matcher.find()) {
            pom = new File(matcher.group(1));
            properties = new File(matcher.group(3));
            refresh = matcher.group(5) != null;
            try {
                addChildWidgets(pom.getPath());
            } catch (Exception e) {
                new FixtureApiPluginException("Unable to add Widget to fitnesse.");
            }

        }
    }


    @Override
    public String asWikiText() throws Exception {
        return FIXTURE_API + " ";
    }

    @Override
    public String getText() throws Exception {
        return "as text";
    }

    @Override
    public String render() throws Exception {
        MavenHelper helper = refresh ? MavenHelper.newInstance(pom, properties) : MavenHelper.instance(pom, properties);
        final StringBuilder sb = new StringBuilder();

        sb.append("Fixtures where Last refreshed at: " + helper.getInstanceDate() + "<br /><br />"); 
        for (String fixture : helper.getFixtures()) {
            sb.append(new FixtureParser().parse(helper.getClass(fixture)));
        }

        return sb.toString();
    }


}
