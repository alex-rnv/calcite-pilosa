package com.alexrnv.calcite.adapter.pilosa.model;

import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaRel;
import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaTableScan;
import com.alexrnv.calcite.adapter.pilosa.pilosa.PilosaAdapter;
import com.google.common.collect.Lists;
import org.apache.calcite.adapter.java.AbstractQueryableTable;
import org.apache.calcite.linq4j.*;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.QueryableTable;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.TranslatableTable;
import org.apache.calcite.schema.impl.AbstractTableQueryable;
import org.apache.calcite.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class PilosaTable extends AbstractQueryableTable implements TranslatableTable, ColumnIndexer {

    private static final String _ID_COLUMN = "_id";

    private final String tableName;
    private final ColumnParametersHolder columnHolder;
    private final PilosaAdapter adapter;

    PilosaTable(PilosaAdapter adapter, String tableName, List<String> columnNames, List<Class<?>> columnJavaClasses) {
        super(Object[].class);
        this.adapter = adapter;
        this.tableName = tableName;
        this.columnHolder = new ColumnParametersHolder(columnNames, columnJavaClasses);
    }

    public String getColumnNameAt(int index) {
        return columnHolder.getColumnNameAt(index);
    }

    @Override
    public int getColumnsNum() {
        return columnHolder.getColumnsNum();
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        return columnHolder.assembleRowType(typeFactory);
    }

    @Override
    public RelNode toRel(RelOptTable.ToRelContext context, RelOptTable relOptTable) {
        final RelOptCluster cluster = context.getCluster();
        return new PilosaTableScan(cluster, cluster.traitSetOf(PilosaRel.CONVENTION), relOptTable, this);
    }

    @Override
    public <T> Queryable<T> asQueryable(QueryProvider queryProvider, SchemaPlus schema, String tableName) {
        return new PilosaQueryable<>(queryProvider, schema, this, tableName);
    }

    private Enumerable<Object> query(String queryString, List<Pair<String, Class>> fields) {
        List<String> header = fields.stream().map(p -> p.left).collect(toList());
        List<Object> list = adapter.queryPilosa(tableName, queryString, header);
        return Linq4j.asEnumerable(list);
    }

    public static class PilosaQueryable<T> extends AbstractTableQueryable<T> {

        PilosaQueryable(QueryProvider queryProvider, SchemaPlus schema,
                               QueryableTable table, String tableName) {
            super(queryProvider, schema, table, tableName);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Enumerator<T> enumerator() {
            final Enumerable<T> enumerable = (Enumerable<T>) getTable().query("", emptyList());
            return enumerable.enumerator();
        }

        private PilosaTable getTable() {
            return (PilosaTable)table;
        }

        public Enumerable<Object> queryPilosa(String queryString, List<Pair<String, Class>> fields) {
            return getTable().query(queryString, fields);
        }
    }

    private static class ColumnParametersHolder {
        private final List<String> columnNames;
        private final List<Class<?>> columnJavaTypes;

        private ColumnParametersHolder(List<String> columnNames, List<Class<?>> columnJavaTypes) {
            this.columnNames = Lists.newArrayList(_ID_COLUMN);
            this.columnNames.addAll(columnNames);
            this.columnJavaTypes = Lists.newArrayList(Long.class);
            this.columnJavaTypes.addAll(columnJavaTypes);
        }

        String getColumnNameAt(int i) {
            return columnNames.get(i);
        }

        int getColumnsNum() {
            return columnNames.size();
        }

        RelDataType assembleRowType(RelDataTypeFactory typeFactory) {
            List<RelDataType> types = new ArrayList<>(columnNames.size());
            for (Class<?> javaClass : columnJavaTypes) {
                RelDataType javaType = typeFactory.createJavaType(javaClass);
                RelDataType sqlType = typeFactory.createSqlType(javaType.getSqlTypeName());
                RelDataType type = typeFactory.createTypeWithNullability(sqlType, true);
                types.add(type);
            }
            return typeFactory.createStructType(Pair.zip(columnNames, types));
        }
    }
}
