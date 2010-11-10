package org.fitnesse.widget;

import fitnesse.wikitext.widgets.MockWidgetRoot;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.fitnesse.widget.SlimFixtureApiTestUtil.getPomFile;
import static org.fitnesse.widget.SlimFixtureApiTestUtil.getPropertyFile;
import static org.fitnesse.widget.SlimFixtureApiWidget.FIXTURE_API;
import static org.fitnesse.widget.SlimFixtureApiWidget.REGEXP;
import static org.apache.commons.lang.StringUtils.contains;
import static org.junit.Assert.*;

/**
 * Test class for the SlimFixtureApiWidget.
 * @author mdasberg
 */
public class SlimFixtureApiWidgetTest {

    private SlimFixtureApiWidget widget;

    @Before
    public void setup() throws Exception {
        widget = new SlimFixtureApiWidget(new MockWidgetRoot(), "!" + FIXTURE_API + " " + getPomFile().getAbsolutePath() + " " + getPropertyFile(true).getAbsolutePath()+ " Y");
    }

    @Test
    public void shouldGetAsWikiText() throws Exception {
        final String asWikiText = widget.asWikiText();
        assertTrue(contains(asWikiText, FIXTURE_API));
    }

    @Test
    public void shouldRender() throws Exception {
        assertNotNull(widget.render());
    }

    @Test
    public void shouldGetAsText() throws Exception {
        assertNotNull(widget.getText());
    }

    @Test
    public void shouldGetCorrectParameters() {
        Pattern pattern = Pattern.compile(REGEXP);
        Matcher matcher = pattern.matcher("!" + FIXTURE_API + " ../../../pom.xml");
        matcher.find();
        assertEquals("../../../pom.xml", matcher.group(1));
        assertNull(matcher.group(3));
        assertNull(matcher.group(5));

        matcher = pattern.matcher("!" + FIXTURE_API + " ../../../pom.xml ../test.properties");
        matcher.find();
        assertEquals("../../../pom.xml", matcher.group(1));
        assertEquals("../test.properties", matcher.group(3));
        assertNull(matcher.group(5));

        matcher = pattern.matcher("!" + FIXTURE_API + " ../../../pom.xml refresh");
        matcher.find();
        assertEquals("../../../pom.xml", matcher.group(1));
        assertNull(matcher.group(3));
        assertEquals("refresh", matcher.group(5));

         matcher = pattern.matcher("!" + FIXTURE_API + " ../../../pom.xml bla");
        matcher.find();
        assertEquals("../../../pom.xml", matcher.group(1));
        assertNull(matcher.group(3));
        assertNull(matcher.group(5));

        matcher = pattern.matcher("!" + FIXTURE_API + " ../../../pom.xml ../test.properties refresh");
        matcher.find();
        assertEquals("../../../pom.xml", matcher.group(1));
        assertEquals("../test.properties", matcher.group(3));
        assertEquals("refresh", matcher.group(5));
    }
}
