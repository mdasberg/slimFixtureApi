FitNesse - slimFixtureAPI
=========================

*"Inspired by Albert Sikkema's FitnesseFixtureApiWidget this widget parses the maven pom.xml file and creates a classloader containing all the dependencies and classes present. The widget then prints out all fitNesse fixtures in Slim table format so that testers can see which fixtures are available for them including the methods. This way the do not have to ask the developer or read javadocs."*

**Motivation**  
In most projects there are loads of fixtures present, but testers do not always know what is present for them. This plugin gives the developer the ability to see every method on every fixture which should eliminate that.

**Plugin installation**  
- copy the slimFixtureApi-<version>-jar-with-dependencies.jar into the directory where fitnesse is located under /plugins (create if it does not exist)  
- create/edit plugins.properties (also where fitnesse is located) and add:  
**WikiWidgets = org.fitnesse.widget.SlimFixtureApiWidget**

**Usage**  
After installation of the plugin add the following to a wiki page:  
**!fixtureApi path-to/pom.xml path-to/<somefile>.properties refresh**

As you can see there are 3 parameters to the widget:  
1: This parameter is required, because this points to the pom.xml which will have to be parsed.  
2: This parameter is not required, but should point to a property file in which we can add fixtureApiExcludes. These excludes need to be in the following format: **package.,package2.**  
3: This parameter is not required, but is a indicator for enabling the refreshing of the classloader for each page refresh.  

**How it works**  
The widget scans all the dependencies, source and test classes and finds all Fixtures. These Fixtures are then rendered in Slim table format. For now only Fixtures that end with the word Fixture are seen as fixtures.

**Improvements/TODO**  
- The filter for fixtures is hardcoded. Could make this configurable
