package com.alexrnv.calcite.adapter.pilosa.model.rels;

import com.alexrnv.calcite.adapter.pilosa.model.ColumnIndexer;
import com.alexrnv.calcite.adapter.pilosa.model.PilosaRelTreeVisitor;
import com.alexrnv.calcite.adapter.pilosa.model.PilosaTableView;
import org.apache.calcite.rel.type.RelDataType;

import java.util.ArrayList;
import java.util.List;

class PilosaGroupByElement implements PilosaElement {

    private final List<Integer> groupByList;
    private final RelDataType inputRelType;

    PilosaGroupByElement(List<Integer> groupByList, RelDataType inputRelType) {
        this.groupByList = groupByList;
        this.inputRelType = inputRelType;
    }

    @Override
    public void accept(PilosaRelTreeVisitor visitor) {
        ColumnIndexer table = visitor.getPilosaTable();
        List<Integer> groupByColumnIndexes = getColumnIndexesForGroupBy(table);
        PilosaTableView tableView = new PilosaTableView(table, groupByColumnIndexes);
        visitor.setPilosaTable(tableView);
    }

    List<Integer> getColumnIndexesForGroupBy(ColumnIndexer table) {
        return mapIndexesFromInputRelToTableIndexes(table, groupByList);
    }

    private List<Integer> mapIndexesFromInputRelToTableIndexes(ColumnIndexer table, List<Integer> columnIndexesInRel) {
        List<Integer> columnIndexesInTable = new ArrayList<>();

        List<String> inputRelColumnNames = inputRelType.getFieldNames();

        for (Integer indexInRel : columnIndexesInRel) {
            String inputRelColumnName = inputRelColumnNames.get(indexInRel);

            for (int indexInTable = 0; indexInTable < table.getColumnsNum(); indexInTable++) {
                String tableColumnName = table.getColumnNameAt(indexInTable);
                if (inputRelColumnName.equalsIgnoreCase(tableColumnName)) {
                    columnIndexesInTable.add(indexInTable);
                }
            }
        }
        return columnIndexesInTable;
    }
}
