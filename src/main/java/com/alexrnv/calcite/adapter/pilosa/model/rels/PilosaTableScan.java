package com.alexrnv.calcite.adapter.pilosa.model.rels;

import com.alexrnv.calcite.adapter.pilosa.model.PilosaRelTreeVisitor;
import com.alexrnv.calcite.adapter.pilosa.model.PilosaTable;
import com.alexrnv.calcite.adapter.pilosa.model.rules.PilosaRules;
import com.alexrnv.calcite.adapter.pilosa.model.rules.PilosaToEnumerableConverterRule;
import org.apache.calcite.adapter.enumerable.EnumerableRules;
import org.apache.calcite.plan.*;
import org.apache.calcite.rel.core.TableScan;

public class PilosaTableScan extends TableScan implements PilosaRel {

    private final PilosaTable pilosaTable;
    private final RelOptTable table;

    public PilosaTableScan(RelOptCluster cluster, RelTraitSet traitSet, RelOptTable table,
                              PilosaTable pilosaTable) {
        super(cluster, traitSet, table);
        this.pilosaTable = pilosaTable;
        this.table = table;
    }

    @Override
    public void accept(PilosaRelTreeVisitor visitor) {
        visitor.setPilosaTable(pilosaTable);
        visitor.setRelOptTable(table);
    }


    @Override
    public void register(RelOptPlanner planner) {
        planner.addRule(PilosaToEnumerableConverterRule.INSTANCE);
        for (RelOptRule rule : PilosaRules.RULES) {
            planner.addRule(rule);
        }

        /*
        This may look like a dirty hack, and maybe it is.
        But I could not find another way to tell Calcite planner
        to prioritize my LogicalIntersection over EnumerableUnion.
        EnumerableUnion is always winning as having better cost.
        On the other hand, using Enumerable convention with Pilosa
        does not make any sense now. So disabling this rule is safe
        at this point. Just need to remember that we do it here in
        PilosaTableScan.
        Similar applies to the join rule.
        PilosaAggregateCorrelateTransposeRule is implemented instead
        to cover the join between pilosa and jdbc scenario.
         */
        planner.removeRule(EnumerableRules.ENUMERABLE_UNION_RULE);
        planner.removeRule(EnumerableRules.ENUMERABLE_JOIN_RULE);
    }

}
