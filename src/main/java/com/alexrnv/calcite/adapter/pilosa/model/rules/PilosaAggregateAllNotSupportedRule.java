package com.alexrnv.calcite.adapter.pilosa.model.rules;

import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaAggregate;
import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaTableScan;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.rel.core.Aggregate;

class PilosaAggregateAllNotSupportedRule extends RelOptRule {
    final static PilosaAggregateAllNotSupportedRule INSTANCE = new PilosaAggregateAllNotSupportedRule();

    PilosaAggregateAllNotSupportedRule() {
        super(operand(PilosaAggregate.class, operand(PilosaTableScan.class, any())));
    }

    @Override
    public void onMatch(RelOptRuleCall call) {
        final Aggregate aggregate = call.rel(0);
        if (aggregate.getGroupSet().isEmpty()) {
            throw new RuntimeException("`count(*)` query without `where` filters or `group by` is not supported");
        }
    }
}
