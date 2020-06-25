package com.alexrnv.calcite.adapter.pilosa.model.rules;

import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaFilter;
import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaRel;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.logical.LogicalFilter;

class PilosaFilterRule extends ConverterRule {
    final static PilosaFilterRule INSTANCE = new PilosaFilterRule();

    private PilosaFilterRule() {
        super(LogicalFilter.class, Convention.NONE, PilosaRel.CONVENTION, "PilosaFilterRule");
    }

    @Override
    public RelNode convert(RelNode rel) {
        final LogicalFilter filter = (LogicalFilter) rel;
        final RelTraitSet traitSet = filter.getTraitSet().replace(PilosaRel.CONVENTION);
        return new PilosaFilter(filter.getCluster(), traitSet,
                convert(filter.getInput(), PilosaRel.CONVENTION), filter.getCondition());
    }
}
