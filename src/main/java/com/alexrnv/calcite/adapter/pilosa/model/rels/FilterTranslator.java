package com.alexrnv.calcite.adapter.pilosa.model.rels;

import com.alexrnv.calcite.adapter.pilosa.model.CorrelationFieldsHolder;
import com.alexrnv.calcite.adapter.pilosa.model.PilosaCalciteModelError;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rex.*;
import org.apache.calcite.sql.SqlKind;

import java.util.*;

import static com.alexrnv.calcite.adapter.pilosa.model.PilosaCalciteModelError.ErrorCode.INTERNAL_ERROR;

class FilterTranslator {

    private final RelDataType rowType;
    private final RexNode condition;
    private final Map<String, RexFieldAccess> correlationParams = new HashMap<>();
    private String translation;

    private FilterTranslator(RelDataType rowType, RexNode condition) {
        this.rowType = rowType;
        this.condition = condition;
    }

    static FilterTranslator of(RelDataType rowType, RexNode condition) {
        return new FilterTranslator(rowType, condition);
    }

    void translate() {
        this.translation = translateOr(condition);
    }

    String getTranslation() {
        return translation;
    }

    void addCorrelationParams(CorrelationFieldsHolder holder) {
        this.correlationParams.forEach(holder::put);
    }

    private String translateOr(RexNode condition) {
        StringJoiner joiner = new StringJoiner(",");
        List<RexNode> orList = RelOptUtil.disjunctions(condition);
        for (RexNode node : orList) {
            joiner.add(translateAnd(node));
        }
        return asUnionString(joiner, orList.size());
    }

    private String asUnionString(StringJoiner joiner, int conditionsNum) {
        if (conditionsNum > 1) {
            return "Union(" + joiner.toString() + ")";
        } else {
            return joiner.toString();
        }
    }

    private String translateAnd(RexNode node0) {
        StringJoiner joiner = new StringJoiner(",");
        List<RexNode> andList = RelOptUtil.conjunctions(node0);
        for (RexNode node : RelOptUtil.conjunctions(node0)) {
            joiner.add(translateExpr(node));
        }

        return asIntersectString(joiner, andList.size());
    }

    private String asIntersectString(StringJoiner joiner, int conditionsNum) {
        if (conditionsNum > 1) {
            return "Intersect(" + joiner.toString() +  ")";
        } else {
            return joiner.toString();
        }
    }

    private String translateExpr(RexNode condition) {
        RexCall call = (RexCall)condition;
        RexNode operand0 = call.operands.get(0);
        RexNode operand1 = call.operands.get(1);

        if (operand0 instanceof RexInputRef) {
            return translateFirstOperandIsRexInputRef(call, (RexInputRef) operand0, operand1);
        } else if (operand0 instanceof RexCall) {
            return translateOr(call);
        } else {
            throw new PilosaCalciteModelError(INTERNAL_ERROR,"unexpected operand type "+ operand0.getClass());
        }
    }

    private String translateFirstOperandIsRexInputRef(RexCall call, RexInputRef firstOperand, RexNode secondOperand) {
        List<String> fieldNames = rowType.getFieldNames();
        int index = firstOperand.getIndex();
        String fieldName = fieldNames.get(index);
        String operation = translateOperation(call);
        Object value = translateSecondOperand(secondOperand);
        return "Row(" + fieldName + operation + value + ")";
    }

    private String translateOperation(RexCall node) {
        SqlKind kind = node.getKind();
        switch (kind) {
            case EQUALS:
            case LESS_THAN:
            case LESS_THAN_OR_EQUAL:
            case GREATER_THAN:
            case GREATER_THAN_OR_EQUAL:
                return kind.sql;
            case NOT_EQUALS:
                return  "!=";
            default:
                throw new PilosaCalciteModelError(INTERNAL_ERROR, "cannot translate " + node);
        }
    }

    private Object translateSecondOperand(RexNode operand) {
        if (operand instanceof RexLiteral) {
            return translateSecondOperandIsLiteral((RexLiteral) operand);
        } else if (operand instanceof RexFieldAccess) {
            return translateSecondOperandIsFieldAccess((RexFieldAccess) operand);
        }
        throw new PilosaCalciteModelError(INTERNAL_ERROR,"unexpected operand type "+ operand.getClass());
    }

    private Object translateSecondOperandIsLiteral(RexLiteral literal) {
        return literal.getValue2();
    }

    /**
     * Field access comes into play when we have {@link org.apache.calcite.adapter.enumerable.EnumerableCorrelate}.
     * We put a placeholder for future value.
     * @see RexFieldAccess
     */
    private String translateSecondOperandIsFieldAccess(RexFieldAccess fieldAccess) {
        String name = fieldAccess.toString();
        correlationParams.put(name, fieldAccess);
        return name;
    }
}
