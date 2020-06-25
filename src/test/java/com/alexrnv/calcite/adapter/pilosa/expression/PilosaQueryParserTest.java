package com.alexrnv.calcite.adapter.pilosa.expression;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static com.alexrnv.calcite.adapter.pilosa.expression.PilosaAggExpression.AggOperation.COUNT;
import static com.alexrnv.calcite.adapter.pilosa.expression.PilosaSetOpExpression.SetOperation.INTERSECT;
import static java.util.Collections.emptyList;

public class PilosaQueryParserTest {

    private final String STUB_FILTER_EXPR_1 = "FILTER_EXPR_1";
    private final String STUB_FILTER_EXPR_2 = "FILTER_EXPR_2";

    @Test
    public void testBuildCountField() {
        String query = buildQueryFromOrderedExpressions(
                new PilosaRowExpression("field"),
                new PilosaAggExpression(COUNT, emptyList())
        );
        Assert.assertEquals("Count(field)", query);
    }

    @Test
    public void testBuildCountUnion() {
        String query = buildQueryFromOrderedExpressions(
                new PilosaFilterExpression(STUB_FILTER_EXPR_1),
                new PilosaAggExpression(COUNT, emptyList())
        );
        Assert.assertEquals("Count(" + STUB_FILTER_EXPR_1 + ")", query);
    }

    @Test
    public void testBuildCountIntersectOfUnions() {
        String query = buildQueryFromOrderedExpressions(
                new PilosaFilterExpression(STUB_FILTER_EXPR_1),
                new PilosaFilterExpression(STUB_FILTER_EXPR_2),
                new PilosaSetOpExpression(INTERSECT, 2),
                new PilosaAggExpression(COUNT, emptyList())
        );
        //note filters are applied in reverse order
        Assert.assertEquals("Count(Intersect(" + STUB_FILTER_EXPR_2 + "," + STUB_FILTER_EXPR_1 + "))", query);
    }

    @Test
    public void testBuildGroupByFilteredByUnion() {
        String query = buildQueryFromOrderedExpressions(
                new PilosaFilterExpression(STUB_FILTER_EXPR_1),
                new PilosaAggExpression(COUNT, Arrays.asList("col1", "col2"))
        );
        Assert.assertEquals("GroupBy(Rows(col1),Rows(col2),filter=" + STUB_FILTER_EXPR_1 + ")", query);
    }

    private String buildQueryFromOrderedExpressions(PilosaExpression... expressions) {
        PilosaQueryBuilder queryBuilder = new PilosaQueryBuilder();
        for (PilosaExpression expression : expressions) {
            queryBuilder.enqueueInReversePolishNotation(expression);
        }
        return queryBuilder.build();
    }

}