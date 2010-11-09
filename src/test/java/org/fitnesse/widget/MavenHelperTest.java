package org.fitnesse.widget;

import org.fitnesse.widget.maven.FixtureApiPluginException;
import org.fitnesse.widget.maven.MavenHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Test class for the MavenHelper.
 * @author mdasberg
 */
public class MavenHelperTest {
    private MavenHelper extractor;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        extractor = MavenHelper.instance(SlimFixtureApiTestUtil.getPomFile(), SlimFixtureApiTestUtil.getPropertyFile(true));
    }

    @Test
    public void shouldThrowExceptionBecausePomDoesNotExist() {
        exception.expect(FixtureApiPluginException.class);
        exception.expectMessage("Pom.xml not found at location:");
        MavenHelper.newInstance(new File("nonExistingPom.xml"), null);
    }

    @Test
    public void shouldThrowExceptionBecausePomIsNull() {
        exception.expect(FixtureApiPluginException.class);
        exception.expectMessage("Pom.xml not specified.");
        MavenHelper.newInstance(null, null);
    }

    @Test
    public void shouldThrowExceptionBecausePomIsNotAFile() {
        exception.expect(FixtureApiPluginException.class);
        exception.expectMessage("Pom.xml is not a file:");
        MavenHelper.newInstance(SlimFixtureApiTestUtil.getPomFile().getParentFile(), null);
    }

    @Test
    public void shouldReturn2Fixtures() {
        Collection<String> fixtures = extractor.getFixtures();
        assertEquals(fixtures.size(), 2);
    }

    @Test
    public void shouldReturn56Fixtures() {
        extractor = MavenHelper.newInstance(SlimFixtureApiTestUtil.getPomFile(), SlimFixtureApiTestUtil.getPropertyFile(false));
        Collection<String> fixtures = extractor.getFixtures();
        assertEquals(fixtures.size(), 56);
    }

    @Test
    public void shouldThrowException() {
        exception.expect(FixtureApiPluginException.class);
        exception.expectMessage("Could not find class for name:");
        extractor.getClass("org.fitnesse.widget.nonExistingFixture");
    }

}
