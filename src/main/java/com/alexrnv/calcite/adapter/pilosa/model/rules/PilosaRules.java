package com.alexrnv.calcite.adapter.pilosa.model.rules;

import org.apache.calcite.plan.*;
import org.apache.calcite.rel.rules.AggregateJoinTransposeRule;
import org.apache.calcite.rel.rules.JoinToCorrelateRule;

public class PilosaRules {

    public static final RelOptRule[] RULES = {
            PilosaProjectRule.INSTANCE,
            PilosaAggregateRule.INSTANCE,
            PilosaFilterRule.INSTANCE,
            PilosaAggregateAllNotSupportedRule.INSTANCE,
            JoinToCorrelateRule.INSTANCE,
            AggregateJoinTransposeRule.INSTANCE,
            PilosaAggregateCorrelateTransposeRule.INSTANCE,
            PilosaAggregateIntersectRule.INSTANCE,
            PilosaAggregateExceptRule.INSTANCE,
    };
}
