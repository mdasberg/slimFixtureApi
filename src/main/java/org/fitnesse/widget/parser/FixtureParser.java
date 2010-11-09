package org.fitnesse.widget.parser;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Fixture class parser.
 * @author mdasberg
 */
public class FixtureParser {
    private SlimHtmlWriter writer;
    private int maxParams = 0;

    /** Constructor. */
    public FixtureParser() {
    }

    /**
     * Parse the giver class.
     * @param fixtureClass The fixture class.
     * @return The parsed output.
     */
    public String parse(Class fixtureClass) {
        writer = new SlimHtmlWriter(fixtureClass.getName());
        parseClazz(fixtureClass);
        updateParams();
        return writer.toString();
    }


    /**
     * Parse the class.
     * @param clazz The class to parse.
     */
    private void parseClazz(Class clazz) {
        Class superclass = clazz.getSuperclass();
        if (superclass != null && superclass != Object.class) {
            parseClazz(superclass);
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers())) {
                List<SlimColumn> params = new ArrayList<SlimColumn>();
                Class[] parameterTypes = method.getParameterTypes();
                if (method.getReturnType() != null) {
                    if (method.getReturnType().getSimpleName().equals("String")) {
                        params.add(new SlimColumn("$string_variable=", 1));
                    } else if (method.getReturnType().getSimpleName().equals("boolean")) {
                        params.add(new SlimColumn("$boolean_variable=", 1));
                    }
                }
                params.add(new SlimColumn(CamelCaseSplitter.splitString(method.getName()), 1));
                for (Class type : parameterTypes) {
                    params.add(new SlimColumn("...", 1));
                }
                if (maxParams < params.size()) {
                    maxParams = params.size();
                }
                writer.addSlimRow(params);
            }
        }
    }

    /** Update the parameters so that all colspaces are correctly filled. */
    private void updateParams() {
        writer.setColspan(maxParams);
        for (List<SlimColumn> row : writer.getSlimRows()) {
            row.get(row.size() - 1).setColspan(maxParams - (row.size() - 2));
        }
    }
}
