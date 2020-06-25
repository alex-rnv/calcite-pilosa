package com.alexrnv.calcite.adapter.pilosa.model.rels;

import com.alexrnv.calcite.adapter.pilosa.expression.PilosaAggExpression.AggOperation;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlKind;

import java.util.List;
import java.util.stream.Collectors;

class PilosaAggregateElementFactory {

    static PilosaElement createFrom(AggregatingRel aggregate) {
        List<Integer> groupByList = aggregate.getGroupByList();
        RelDataType inputRelType = aggregate.getInputRowType();
        List<AggOperation> operations = getOperations(aggregate);

        if(!operations.isEmpty()) {
            return new PilosaAggregateElement(groupByList, inputRelType, operations);
        } else {
            return new PilosaGroupByElement(groupByList, inputRelType);
        }
    }

    private static List<AggOperation> getOperations(AggregatingRel aggregate) {
        return aggregate.getAggCallList().stream().map(aggCall -> {
            SqlKind kind = aggCall.getAggregation().getKind();
            return AggOperation.fromSqlKind(kind);
        }).collect(Collectors.toList());
    }
}
