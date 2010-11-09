package org.fitnesse.widget.testdata;

/** @author mischa */
public class CatFixture extends Animal {

    public CatFixture() {
    }

    /**
     * Lets the cat say hello.
     * @return hello The result.
     */
    public String hello() {
        return "miauw";
    }

    /**
     * Lets the cat say goodbye.
     * @return goodbye The result.
     */
    public String goodbye() {
        return "miauwwww";
    }


    public boolean isAlive() {
        return true;
    }
}
