package com.alexrnv.calcite.adapter.pilosa.model.rules;

import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaAggregate;
import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaRel;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.logical.LogicalAggregate;

class PilosaAggregateRule extends ConverterRule {
    final static PilosaAggregateRule INSTANCE = new PilosaAggregateRule();

    private PilosaAggregateRule() {
        super(LogicalAggregate.class, Convention.NONE, PilosaRel.CONVENTION, "PilosaAggregateRule");
    }

    @Override
    public RelNode convert(RelNode rel) {
        final LogicalAggregate agg = (LogicalAggregate) rel;
        final RelTraitSet traitSet = agg.getTraitSet().replace(PilosaRel.CONVENTION);
        return new PilosaAggregate(agg.getCluster(), traitSet,
                convert(agg.getInput(), PilosaRel.CONVENTION),
                agg.getGroupSet(),
                agg.getGroupSets(),
                agg.getAggCallList());
    }
}
