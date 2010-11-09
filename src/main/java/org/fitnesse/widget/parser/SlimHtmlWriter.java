package org.fitnesse.widget.parser;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.trim;

/**
 * Slim Html writer which creates a slim table for the given class.
 * @author mdasberg
 */
public class SlimHtmlWriter {
    private String className;
    private List<List<SlimColumn>> rows = new ArrayList<List<SlimColumn>>();
    private int colspan;

    /**
     * Constructor.
     * @param className The class name.
     */
    public SlimHtmlWriter(String className) {
        this.className = className;
    }

    /**
     * Add a Slim row.
     * @param params The params.
     */
    public void addSlimRow(List<SlimColumn> params) {
        rows.add(params);
    }

    /**
     * Set the colspan.
     * @param colspan The colspan.
     */
    public void setColspan(int colspan) {
        this.colspan = colspan;
    }

    @Override
    public String toString() {
        String template;
        try {
            template = IOUtils.toString(this.getClass().getResourceAsStream("/slimTemplate.st"));
            final StringTemplate st = new StringTemplate(template);
            st.setAttribute("colspan", colspan);
            st.setAttribute("className", className);
            st.setAttribute("methods", rows);
            return cleanUp(st.toString());
        } catch (final IOException e) {
            throw new RuntimeException("Error occured while creating html output", e);
        }
    }

    /**
     * Cleans the html.
     * @param formattedHtml The html.
     * @return html The cleandup html.
     */
    private String cleanUp(final String formattedHtml) {
        return trim(formattedHtml);
    }

    /**
     * Gets the slim rows.
     * @return rows The slim rows.
     */
    public List<List<SlimColumn>> getSlimRows() {
        return rows;
    }
}
