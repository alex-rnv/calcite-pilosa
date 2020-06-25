package com.alexrnv.calcite.adapter.pilosa.model;

import org.apache.calcite.linq4j.tree.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.StringJoiner;

public class Linq4jTest {

    private static class StringHolder {
        private final String string;

        private StringHolder(String string) {
            this.string = string;
        }

        public String getString() {
            return string;
        }
    }

    @Test
    public void learningTestBlockBuilder() {
        BlockBuilder blockBuilder = new BlockBuilder();
        Expression joiner = blockBuilder.append("joiner", Expressions.new_(StringJoiner.class));
        Expression holder = blockBuilder.append("holder", Expressions.new_(StringHolder.class, Expressions.constant("2")));

        joiner = blockBuilder.append("joiner", Expressions.call(joiner, "add", Expressions.convert_(Expressions.call(holder, "getString"), CharSequence.class)));
        joiner = blockBuilder.append("joiner", Expressions.call(joiner, "add", Expressions.constant("1", CharSequence.class)));

        blockBuilder.add(Expressions.call(joiner, "toString"));

        String block = blockBuilder.toBlock().toString();
        Assert.assertEquals("{\n" +
                "  final java.util.StringJoiner joiner = new java.util.StringJoiner();\n" +
                "  final com.alexrnv.calcite.adapter.pilosa.model.Linq4jTest.StringHolder holder = new com.alexrnv.calcite.adapter.pilosa.model.Linq4jTest.StringHolder(\n" +
                "    \"2\");\n" +
                "  return joiner.add((CharSequence) holder.getString()).add(\"1\").toString();\n" +
                "}\n", block);
    }
}
