package org.fitnesse.widget.parser;

/**
 * Slim column which represents a single column in the slim table.
 * @author mdasberg
 */
public class SlimColumn {
    private String name;
    private int colspan;

    /**
     * Constructor.
     * @param name The name.
     * @param colspan The colspan.
     */
    public SlimColumn(String name, int colspan) {
        this.name = name;
        this.colspan = colspan;
    }

    /**
     * Get the name of the column.
     * @return column The column.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the colspan.
     * @return clspan The colspan.
     */
    public int getColspan() {
        return colspan;
    }

    /**
     * Set the colspan.
     * @param colspan The colspan.
     */
    public void setColspan(int colspan) {
        this.colspan = colspan;
    }
}