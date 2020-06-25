package com.alexrnv.calcite.adapter.pilosa.model;

import com.alexrnv.calcite.adapter.pilosa.commons.StringUtils;
import org.apache.calcite.adapter.enumerable.EnumerableRelImplementor;
import org.apache.calcite.adapter.enumerable.RexToLixTranslator;
import org.apache.calcite.linq4j.tree.BlockBuilder;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.rex.RexCorrelVariable;
import org.apache.calcite.rex.RexFieldAccess;

import java.util.*;

class PilosaToLinq4jExpressionConverter {

    private final EnumerableRelImplementor implementor;
    private final CorrelationFieldsHolder correlationFieldsHolder;
    private final List<String> queryParts;


    PilosaToLinq4jExpressionConverter(EnumerableRelImplementor implementor, CorrelationFieldsHolder correlationFieldsHolder, String queryString) {
        Objects.requireNonNull(queryString);
        this.implementor = implementor;
        this.correlationFieldsHolder = correlationFieldsHolder;
        this.queryParts = separateCorrelationFields(queryString, correlationFieldsHolder.getAllParamNames());
    }

    private static List<String> separateCorrelationFields(String queryString, Set<String> corrFieldNames) {
        return StringUtils.splitKeepingDelimiters(queryString, corrFieldNames);
    }

    Expression appendToBlock(BlockBuilder blockBuilder) {
        if (hasNoCorrFields()) {
            return createConstantSingleValueExpression();
        } else {
            return createExpressionByJoiningWithCorrFieldGetters(blockBuilder);
        }
    }

    private boolean hasNoCorrFields() {
        return queryParts.size() == 1;
    }

    private Expression createConstantSingleValueExpression() {
        return Expressions.constant(queryParts.get(0));
    }

    private Expression createExpressionByJoiningWithCorrFieldGetters(BlockBuilder blockBuilder) {
        Expression joiner = blockBuilder.append("joiner", Expressions.new_(StringJoiner.class, Expressions.constant("")));
        joiner = replacePartsWithCorrFieldGettersIfRequired(blockBuilder, joiner);
        return blockBuilder.append("query", Expressions.call(joiner, "toString"));
    }

    private Expression replacePartsWithCorrFieldGettersIfRequired(BlockBuilder blockBuilder, Expression joiner) {
        for (String queryPart : queryParts) {
            joiner = replaceIfMatchingCorrFieldExists(blockBuilder, joiner, queryPart);
        }
        return joiner;
    }

    private Expression replaceIfMatchingCorrFieldExists(BlockBuilder blockBuilder, Expression joiner, String queryPart) {
        for (String name : correlationFieldsHolder.getAllParamNames()) {
            if (name.equalsIgnoreCase(queryPart)) {
                joiner = addCorrFieldGetterExpression(blockBuilder, joiner, name);
            } else {
                joiner = addConstantExpression(blockBuilder, joiner, queryPart);
            }
        }
        return joiner;
    }

    private Expression addCorrFieldGetterExpression(BlockBuilder blockBuilder, Expression joiner, String name) {
        RexFieldAccess fieldAccess = correlationFieldsHolder.get(name);
        Expression corrFieldGetterExpression = createCorrFieldGetterExpression(blockBuilder, fieldAccess);
        joiner = blockBuilder.append("joiner", Expressions.call(joiner, "add", corrFieldGetterExpression));
        return joiner;
    }

    private Expression addConstantExpression(BlockBuilder blockBuilder, Expression joiner, String queryPart) {
        Expression constantExpression = Expressions.constant(queryPart, CharSequence.class);
        joiner = blockBuilder.append("joiner", Expressions.call(joiner, "add", constantExpression));
        return joiner;
    }

    private Expression createCorrFieldGetterExpression(BlockBuilder blockBuilder, RexFieldAccess fieldAccess) {
        RexCorrelVariable corrVar = (RexCorrelVariable) fieldAccess.getReferenceExpr();
        int fieldIndex = fieldAccess.getField().getIndex();
        RexToLixTranslator.InputGetter getter = implementor.getCorrelVariableGetter(corrVar.getName());
        return getter.field(blockBuilder, fieldIndex, CharSequence.class);
    }

}
