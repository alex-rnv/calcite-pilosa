package com.alexrnv.calcite.adapter.pilosa.model;

import org.apache.calcite.test.CalciteAssert;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

class PilosaAssert {

    private String sql;
    private String model = TestFixtures.STUB_PILOSA_MODEL;

    private List<String> expectedPilosaExpressions = new ArrayList<>();
    private List<String> actualPilosaExpressions = new ArrayList<>();


    PilosaAssert withSql(String sql) {
        this.sql = sql;
        return this;
    }

    PilosaAssert withModel(String model) {
        this.model = model;
        return this;
    }

    PilosaAssert expectPilosaExpression(String pilosaExpression) {
        this.expectedPilosaExpressions = prepareExpectedPilosaExpression(pilosaExpression, "", Collections.singletonList(""));
        return this;
    }

    PilosaAssert expectPilosaExpressions(String pilosaExpression, String paramPlaceholder, List<String> params) {
        this.expectedPilosaExpressions = prepareExpectedPilosaExpression(pilosaExpression, paramPlaceholder, params);
        return this;
    }

    void run() {
        setMatchingQueryCheckerCallback();
        try {
            executeAndValidate();
        } finally {
            resetMatchingQueryCheckerCallback();
        }
    }

    private void setMatchingQueryCheckerCallback() {
        setStubAdapterCallback(expr -> {
            actualPilosaExpressions.add(expr);
            return null;
        });
    }

    private void resetMatchingQueryCheckerCallback() {
        StubPilosaAdapter.INSTANCE.setCallback(null);
    }

    private void executeAndValidate() {
        execute();
        validate();
    }

    private void execute() {
        CalciteAssert.that()
                .withModel(model)
                .with("caseSensitive", "false")
                .query(sql)
                .returnsCount(expectedPilosaExpressions.size());
    }

    private void validate() {
        if (expectedPilosaExpressions.isEmpty() && actualPilosaExpressions.isEmpty())
            return;

        Assert.assertEquals(expectedPilosaExpressions, actualPilosaExpressions);
    }

    private List<String> prepareExpectedPilosaExpression(String pilosaExpression, String paramPlaceholder, List<String> params) {
        return params.stream().map(p -> pilosaExpression.replace(paramPlaceholder, p)).collect(Collectors.toList());
    }


    private void setStubAdapterCallback(Function<String, String> callback) {
        StubPilosaAdapter.INSTANCE.setCallback(callback);
    }

}
