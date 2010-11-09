package org.fitnesse.widget.testdata;

/** @author mischa */
public class Animal {
    private String name;

    public void myNameIs(String name) {
        this.name = name;
    }

    public String whatIsMyName() {
        return name;
    }
}
