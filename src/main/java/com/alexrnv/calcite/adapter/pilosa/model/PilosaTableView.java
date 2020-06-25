package com.alexrnv.calcite.adapter.pilosa.model;


import java.util.List;

/**
 * A view on top of a table to access only specified columns.
 */
public class PilosaTableView implements ColumnIndexer {

    private final List<Integer> columnIndexes;
    private final ColumnIndexer table;

    public PilosaTableView(ColumnIndexer table, List<Integer> columnIndexes) {
        this.table = table;
        this.columnIndexes = columnIndexes;
    }

    @Override
    public String getColumnNameAt(int index) {
        return this.table.getColumnNameAt(columnIndexes.get(index));
    }

    @Override
    public int getColumnsNum() {
        return columnIndexes.size();
    }

}
