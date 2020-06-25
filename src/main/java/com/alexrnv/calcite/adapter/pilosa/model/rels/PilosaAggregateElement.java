package com.alexrnv.calcite.adapter.pilosa.model.rels;

import com.alexrnv.calcite.adapter.pilosa.model.ColumnIndexer;
import com.alexrnv.calcite.adapter.pilosa.model.PilosaRelTreeVisitor;
import com.alexrnv.calcite.adapter.pilosa.expression.PilosaAggExpression;
import com.alexrnv.calcite.adapter.pilosa.expression.PilosaAggExpression.AggOperation;
import org.apache.calcite.rel.type.RelDataType;

import java.util.List;
import java.util.stream.Collectors;

import static com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaLimitations.assertAggCalls;

class PilosaAggregateElement extends PilosaGroupByElement {

    private final List<AggOperation> operations;

    PilosaAggregateElement(List<Integer> groupByList, RelDataType inputRelType, List<AggOperation> operations) {
        super(groupByList, inputRelType);
        this.operations = operations;
    }

    @Override
    public void accept(PilosaRelTreeVisitor visitor) {
        ColumnIndexer table = visitor.getPilosaTable();
        List<String> groupByList = getGroupByColumnNames(table);

        assertAggCalls(operations);

        for (AggOperation operation : operations) {
            visitor.enqueueInReversePolishNotation(new PilosaAggExpression(operation, groupByList));
        }
    }

    private List<String> getGroupByColumnNames(ColumnIndexer table) {
        List<Integer> groupByIndexList = getColumnIndexesForGroupBy(table);
        //always sort column names, so we have same order all the time, which is easier to test
        return groupByIndexList.stream().map(table::getColumnNameAt).sorted(String::compareTo).collect(Collectors.toList());
    }
}
