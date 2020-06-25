package com.alexrnv.calcite.adapter.pilosa.model;

import org.apache.calcite.rel.type.RelDataType;

/**
 * TODO:
 * This should be doable with {@link RelDataType} and {@link org.apache.calcite.rel.type.RelDataTypeFactory}
 */
public interface ColumnIndexer {
    String getColumnNameAt(int index);
    int getColumnsNum();
}
