package com.alexrnv.calcite.adapter.pilosa.model.rules;

import com.alexrnv.calcite.adapter.pilosa.model.PilosaToEnumerableConverter;
import com.alexrnv.calcite.adapter.pilosa.model.rels.*;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelOptRuleOperand;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Aggregate;
import org.apache.calcite.rel.core.SetOp;

import java.util.Collections;
import java.util.List;

abstract class PilosaAggregateSetOpRule extends RelOptRule {

    private final SetOperationsFolder setOperationsFolder;

    PilosaAggregateSetOpRule(RelOptRuleOperand operand, SetOperationsFolder setOperationsFolder) {
        super(operand);
        this.setOperationsFolder = setOperationsFolder;
    }

    @Override
    public void onMatch(RelOptRuleCall call) {
        Aggregate aggregate = call.rel(0);
        SetOp setOp = call.rel(1);
        PilosaToEnumerableConverter converter = call.rel(2);

        RelNode node = transformToPilosaRels(aggregate, setOp, converter);
        if (node != null) {
            call.transformTo(node);
        }
    }

    /**
     * Transforms EnumerableAggregate->EnumerableSetOp->PilosaToEnumerableConverter->[PilosaRel, PilosaRel,..]
     * into       PilosaToEnumerableConverter->PilosaAggregate->PilosaSetOp->[PilosaRel, PilosaRel,..]
     */
    private RelNode transformToPilosaRels(Aggregate aggregate, RelNode setOperation, PilosaToEnumerableConverter converter) {
        List<RelNode> inputs = setOperation.getInputs();
        if (inputs.isEmpty()) return null;

        RelNode foldedNode = fold(inputs);

        RelTraitSet traits = converter.getTraitSet().replace(PilosaRel.CONVENTION);

        PilosaAggregate pilosaAggregate = new PilosaAggregate(aggregate.getCluster(), traits, foldedNode,
                aggregate.getGroupSet(), aggregate.getGroupSets(), aggregate.getAggCallList());

        return converter.copy(converter.getTraitSet(), Collections.singletonList(pilosaAggregate));
    }

    private RelNode fold(List<RelNode> inputs) {
        RelNode rel = inputs.get(0);
        RelNode folded = toPilosaConvention(rel);
        for (int i = 1; i < inputs.size(); i++) {
            rel = inputs.get(i);
            folded = setOperationsFolder.fold(folded, toPilosaConvention(rel));
        }
        return folded;
    }

    private RelNode toPilosaConvention(RelNode rel) {
        return RelOptRule.convert(rel, PilosaRel.CONVENTION);
    }
}
