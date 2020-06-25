package com.alexrnv.calcite.adapter.pilosa.model;

import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaRel;
import com.alexrnv.calcite.adapter.pilosa.expression.PilosaExpression;
import com.alexrnv.calcite.adapter.pilosa.expression.PilosaQueryBuilder;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;

/**
 * Callback for the implementation process that converts a tree of
 * {@link PilosaRel} nodes into a Pilosa expressions queue.
 * */
public class PilosaRelTreeVisitor {

    private final PilosaQueryBuilder pilosaQueryBuilder;

    private RelOptTable relOptTable;
    private ColumnIndexer pilosaTable;
    private CorrelationFieldsHolder correlationFieldsHolder = new CorrelationFieldsHolder();

    public PilosaRelTreeVisitor(PilosaQueryBuilder pilosaQueryBuilder) {
        this.pilosaQueryBuilder = pilosaQueryBuilder;
    }

    public RelOptTable getRelOptTable() {
        return relOptTable;
    }

    public void setRelOptTable(RelOptTable relOptTable) {
        this.relOptTable = relOptTable;
    }

    public ColumnIndexer getPilosaTable() {
        return pilosaTable;
    }

    public void setPilosaTable(ColumnIndexer pilosaTable) {
        this.pilosaTable = pilosaTable;
    }


    public void enqueueInReversePolishNotation(PilosaExpression expression) {
        this.pilosaQueryBuilder.enqueueInReversePolishNotation(expression);
    }

    public void visitChild(RelNode input) {
        ((PilosaRel) input).accept(this);
    }

    public CorrelationFieldsHolder getCorrelationFieldsHolder() {
        return correlationFieldsHolder;
    }
}
