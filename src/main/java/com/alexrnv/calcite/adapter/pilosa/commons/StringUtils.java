package com.alexrnv.calcite.adapter.pilosa.commons;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class StringUtils {

    /*
     * Some logic relies on a fact that letter ý will never be present in input string.
     * Since this module is dealing with SQL strings, we have all rights to expect this condition satisfies.
     */
    static final String IMPOSSIBLE_IN_SQL_SIGN = "\u00FD";

    public static List<String> splitKeepingDelimiters(String string, Collection<String> delimiters) {
        Objects.requireNonNull(string);
        Objects.requireNonNull(delimiters);

        if (string.contains(IMPOSSIBLE_IN_SQL_SIGN)) {
            throw new RuntimeException("method splitKeepingDelimiters() is not designed to work with UTF strings");
        }
        for (String delimiter : delimiters) {
            string = string.replace(delimiter, IMPOSSIBLE_IN_SQL_SIGN + delimiter + IMPOSSIBLE_IN_SQL_SIGN);
        }
        return Arrays.asList(string.split(IMPOSSIBLE_IN_SQL_SIGN));
    }
}
