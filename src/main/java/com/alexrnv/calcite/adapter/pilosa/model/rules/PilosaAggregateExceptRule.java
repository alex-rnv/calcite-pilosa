package com.alexrnv.calcite.adapter.pilosa.model.rules;

import com.alexrnv.calcite.adapter.pilosa.model.PilosaToEnumerableConverter;
import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaExcept;
import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaRel;
import org.apache.calcite.adapter.enumerable.EnumerableAggregate;
import org.apache.calcite.adapter.enumerable.EnumerableMinus;
import org.apache.calcite.rel.RelNode;

import java.util.Arrays;

class PilosaAggregateExceptRule extends PilosaAggregateSetOpRule {
    final static PilosaAggregateExceptRule INSTANCE = new PilosaAggregateExceptRule();

    private PilosaAggregateExceptRule() {
        super(
                operand(EnumerableAggregate.class,
                        operand(EnumerableMinus.class,
                                operand(PilosaToEnumerableConverter.class,
                                        operand(PilosaRel.class, any()))
                        )
                ),
                new SetOperationsFolder() {
                    @Override
                    protected RelNode foldProjectProject(RelNode leftProject, RelNode rightProject) {
                        return new PilosaExcept(leftProject.getCluster(), leftProject.getTraitSet(),
                                leftProject.getRowType(), Arrays.asList(leftProject, rightProject));
                    }
                });
    }

}
