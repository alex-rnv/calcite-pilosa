package com.alexrnv.calcite.adapter.pilosa.model.rules;

import com.alexrnv.calcite.adapter.pilosa.model.PilosaToEnumerableConverter;
import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaIntersect;
import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaRel;
import org.apache.calcite.adapter.enumerable.EnumerableAggregate;
import org.apache.calcite.adapter.enumerable.EnumerableIntersect;
import org.apache.calcite.rel.RelNode;

import java.util.Arrays;

class PilosaAggregateIntersectRule extends PilosaAggregateSetOpRule {
    final static PilosaAggregateIntersectRule INSTANCE = new PilosaAggregateIntersectRule();

    private PilosaAggregateIntersectRule() {
        super(
                operand(EnumerableAggregate.class,
                        operand(EnumerableIntersect.class,
                                operand(PilosaToEnumerableConverter.class,
                                        operand(PilosaRel.class, any()))
                        )
                ),
                new SetOperationsFolder() {
                    @Override
                    protected RelNode foldProjectProject(RelNode leftProject, RelNode rightProject) {
                        return new PilosaIntersect(leftProject.getCluster(), leftProject.getTraitSet(),
                                leftProject.getRowType(), Arrays.asList(leftProject, rightProject));
                    }
                });
    }

}
