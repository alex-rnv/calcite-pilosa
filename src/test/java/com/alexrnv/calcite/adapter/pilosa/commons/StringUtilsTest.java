package com.alexrnv.calcite.adapter.pilosa.commons;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.alexrnv.calcite.adapter.pilosa.commons.StringUtils.IMPOSSIBLE_IN_SQL_SIGN;


public class StringUtilsTest {

    @Test(expected = NullPointerException. class)
    public void testSplitFailsOnNullString() {
        StringUtils.splitKeepingDelimiters(null, Collections.emptyList());
    }

    @Test(expected = NullPointerException. class)
    public void testSplitFailsOnNullDelimiters() {
        StringUtils.splitKeepingDelimiters("Count(Row(language=\"2\"))", null);
    }

    @Test(expected = RuntimeException. class)
    public void testSplitFailsIfStringContainsForbiddenLetter() {
        StringUtils.splitKeepingDelimiters("Count(Row(language=\"" + IMPOSSIBLE_IN_SQL_SIGN + "\"))", null);
    }

    @Test
    public void testSplitNoDelimiters() {
        List<String> result = StringUtils.splitKeepingDelimiters("Count(Row(language=\"2\"))", Collections.emptyList());
        List<String> expected = new ArrayList<>();
        expected.add("Count(Row(language=\"2\"))");
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testSplitOneParameter() {
        List<String> result = StringUtils.splitKeepingDelimiters("Count(Row(language=\"$cor0.language\"))", Collections.singletonList("$cor0.language"));
        List<String> expected = new ArrayList<>();
        expected.add("Count(Row(language=\"");
        expected.add("$cor0.language");
        expected.add("\"))");
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testSplitTwoParameters() {
        List<String> result = StringUtils.splitKeepingDelimiters("Count(Union(Row(language=\"$cor0.language\"),Row(quantity=\"$cor1.quantity\")))", Arrays.asList("$cor0.language", "$cor1.quantity"));
        List<String> expected = new ArrayList<>();
        expected.add("Count(Union(Row(language=\"");
        expected.add("$cor0.language");
        expected.add("\"),Row(quantity=\"");
        expected.add("$cor1.quantity");
        expected.add("\")))");
        Assert.assertEquals(expected, result);
    }

}