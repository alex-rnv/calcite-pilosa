package com.alexrnv.calcite.adapter.pilosa.model.rels;

import com.alexrnv.calcite.adapter.pilosa.model.PilosaRelTreeVisitor;
import com.alexrnv.calcite.adapter.pilosa.expression.PilosaSetOpExpression;
import com.alexrnv.calcite.adapter.pilosa.expression.PilosaSetOpExpression.SetOperation;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.AbstractRelNode;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataType;

import java.util.List;
import java.util.ListIterator;

public abstract class PilosaSetOp extends AbstractRelNode implements PilosaRel {
    private final RelDataType rowType;
    private final List<RelNode> inputs;

    PilosaSetOp(RelOptCluster cluster, RelTraitSet traitSet, RelDataType rowType, List<RelNode> inputs) {
        super(cluster, traitSet);
        this.rowType = rowType;
        this.inputs = inputs;
    }

    @Override
    public void accept(PilosaRelTreeVisitor visitor) {
        visitChildren(visitor);
        enqueueIntersectOperation(visitor);
    }

    /*
    It is important to visit children in reverse order, as visitor puts them in stack.
    So after reading from stack initial order will be restored.
    Order matters for some operations, like EXCEPT, but does not for others, like INTERSECT.
     */
    private void visitChildren(PilosaRelTreeVisitor visitor) {
        ListIterator<RelNode> reverseIterator = inputs.listIterator(inputs.size());
        while (reverseIterator.hasPrevious()) {
            visitor.visitChild(reverseIterator.previous());
        }
    }

    private void enqueueIntersectOperation(PilosaRelTreeVisitor visitor) {
        visitor.enqueueInReversePolishNotation(new PilosaSetOpExpression(getOperationType(), inputs.size()));
    }

    protected abstract SetOperation getOperationType();

    @Override
    public void replaceInput(int ordinalInParent, RelNode p) {
        this.inputs.set(ordinalInParent, p);
    }

    @Override
    public List<RelNode> getInputs() {
        return inputs;
    }

    @Override
    public RelDataType deriveRowType() {
        return rowType;
    }
}
