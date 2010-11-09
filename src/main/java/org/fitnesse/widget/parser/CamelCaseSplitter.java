package org.fitnesse.widget.parser;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.join;

/**
 * Camel Case Splitter.
 * @author asikkema
 */
public final class CamelCaseSplitter {

    /**
     * Splits the camelcased string.
     * @param camelcased The camelcased string.
     * @return splitString The split camelcased string.
     */
    public static String splitString(final String camelcased) {
        return join(splitIntoWords(camelcased), " ");
    }

    /**
     * Splits the camelcased string into words.
     * @param camelcased The camelcased string.
     * @return splitWords The split words.
     */
    private static List<String> splitIntoWords(String camelcased) {
        List<String> words = new ArrayList<String>();
        String word = "";
        for (int i = 0; i < camelcased.length(); i++) {
            Character c = camelcased.charAt(i);
            if (i > 0 && isUpper(c) && !isUpper(camelcased.charAt(i - 1))) {
                words.add(word);
                word = "" + Character.toLowerCase(c);
            } else {
                word += c;
            }
        }
        words.add(word);
        return words;
    }

    /**
     * Indicates if a char is uppercased.
     * @param c The character.
     * @return <code>true</code> in case the char is upper case, else <code>false</code>. 
     */
    private static boolean isUpper(final char c) {
        return Character.isUpperCase(c);
    }

}