package com.alexrnv.calcite.adapter.pilosa.model.rels;

import com.alexrnv.calcite.adapter.pilosa.expression.PilosaSetOpExpression.SetOperation;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataType;

import java.util.List;

import static com.alexrnv.calcite.adapter.pilosa.expression.PilosaSetOpExpression.SetOperation.INTERSECT;

public class PilosaIntersect extends PilosaSetOp {

    public PilosaIntersect(RelOptCluster cluster, RelTraitSet traitSet, RelDataType rowType, List<RelNode> inputs) {
        super(cluster, traitSet, rowType, inputs);
        assert !inputs.isEmpty();
    }

    @Override
    protected SetOperation getOperationType() {
        return INTERSECT;
    }

    @Override
    public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
        return new PilosaIntersect(getCluster(), traitSet, deriveRowType() , inputs);
    }

}
