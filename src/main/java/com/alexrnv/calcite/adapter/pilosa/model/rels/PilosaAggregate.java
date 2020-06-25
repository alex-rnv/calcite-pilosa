package com.alexrnv.calcite.adapter.pilosa.model.rels;

import com.alexrnv.calcite.adapter.pilosa.model.PilosaRelTreeVisitor;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Aggregate;
import org.apache.calcite.rel.core.AggregateCall;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.util.ImmutableBitSet;

import java.util.List;


public class PilosaAggregate extends Aggregate implements PilosaRel, AggregatingRel {

    public PilosaAggregate(RelOptCluster cluster, RelTraitSet traits, RelNode child,
                              ImmutableBitSet groupSet, List<ImmutableBitSet> groupSets, List<AggregateCall> aggCalls) {
        super(cluster, traits, child, groupSet, groupSets, aggCalls);
    }

    @Override
    public void accept(PilosaRelTreeVisitor visitor) {
        visitChild(visitor);
        visitSelf(visitor);
    }

    private void visitChild(PilosaRelTreeVisitor visitor) {
        visitor.visitChild(getInput());
    }

    private void visitSelf(PilosaRelTreeVisitor visitor) {
        PilosaAggregateElementFactory.createFrom(this).accept(visitor);
    }

    @Override
    public Aggregate copy(RelTraitSet traitSet, RelNode input, ImmutableBitSet groupSet, List<ImmutableBitSet> groupSets, List<AggregateCall> aggCalls) {
        return new PilosaAggregate(getCluster(), traitSet, input, groupSet, groupSets, aggCalls);
    }

    @Override
    public List<Integer> getGroupByList() {
        return groupSet.asList();
    }

    @Override
    public RelDataType getInputRowType() {
        return getInput().getRowType();
    }
}
