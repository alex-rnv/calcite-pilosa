package com.alexrnv.calcite.adapter.pilosa.model.rules;

import com.alexrnv.calcite.adapter.pilosa.model.PilosaToEnumerableConverter;
import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaAggregate;
import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaRel;
import com.google.common.collect.ImmutableList;
import org.apache.calcite.adapter.enumerable.EnumerableAggregate;
import org.apache.calcite.adapter.enumerable.EnumerableCorrelate;
import org.apache.calcite.adapter.enumerable.EnumerableProject;
import org.apache.calcite.adapter.jdbc.JdbcToEnumerableConverter;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.core.Aggregate;
import org.apache.calcite.rel.core.Correlate;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexUtil;
import org.apache.calcite.util.ImmutableBitSet;
import org.apache.calcite.util.Litmus;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.alexrnv.calcite.adapter.pilosa.model.rules.RuleUtils.deriveGroupingIndexesFromInputAndOutputRows;

class PilosaAggregateCorrelateTransposeRule extends RelOptRule {
    final static PilosaAggregateCorrelateTransposeRule INSTANCE = new PilosaAggregateCorrelateTransposeRule();

    PilosaAggregateCorrelateTransposeRule() {
        super(
                operand(EnumerableAggregate.class,
                        operand(EnumerableAggregate.class,
                                operand(EnumerableCorrelate.class,
                                        operand(JdbcToEnumerableConverter.class, any()),
                                        operand(PilosaToEnumerableConverter.class, operand(PilosaRel.class, any()))
                                )
                        )
                )
        );
    }

    /*
    Pushing aggregate operations under Pilosa:
    translate
           Agg(Count)->Agg(Grouping)->Corr--->jdbcEnum
                                           \->PilosaEnum->PilosaRel
    into
           Project---->Corr--->jdbcEnum
                            \->PilosaEnum->PilosaAgg(Count)->PilosaAgg(Grouping)->PilosaRel
     */
    @Override
    public void onMatch(RelOptRuleCall call) {
        final Aggregate countingAggregate = call.rel(0);
        final Aggregate groupingAggregate = call.rel(1);
        final Correlate correlate = call.rel(2);
        final JdbcToEnumerableConverter jdbcToEnumerableConverter = call.rel(3);
        final PilosaToEnumerableConverter pilosaToEnumerableConverter = call.rel(4);
        final PilosaRel pilosaRel = call.rel(5);

        PilosaAggregate pilosaGroupingAggregate = convertEnumGroupingAggregateIntoPilosaGroupingAggregate(groupingAggregate, pilosaRel, correlate.getRowType());
        PilosaAggregate pilosaCountingAggregate = convertEnumCountingAggregateIntoPilosaCountingAggregate(countingAggregate, pilosaGroupingAggregate);
        PilosaToEnumerableConverter pilosaEnumConverterWithCountingAggregate = linkPilosaEnumConverterWithCountingAggregate(pilosaToEnumerableConverter, pilosaCountingAggregate);
        EnumerableCorrelate correlateWithNewPilosaEnumConverter = linkCorrelateWithNewPilosaEnumConverter(correlate, jdbcToEnumerableConverter, pilosaEnumConverterWithCountingAggregate);
        try {
            EnumerableProject enumerableProject = createEnumerableProject(countingAggregate, correlateWithNewPilosaEnumConverter);
            call.transformTo(enumerableProject);
        } catch (WrongPlanError e) {
            //
        }

    }

    private PilosaAggregate convertEnumGroupingAggregateIntoPilosaGroupingAggregate(Aggregate aggregateGroupBy, PilosaRel input, RelDataType outputRowType) {
        RelDataType inputRowType = input.getRowType();
        ImmutableBitSet pilosaAggGroupByBitSet = ImmutableBitSet.of(deriveGroupingIndexesFromInputAndOutputRows(inputRowType, outputRowType));
        RelTraitSet pilosaTraits = aggregateGroupBy.getTraitSet().replace(PilosaRel.CONVENTION);

        return new PilosaAggregate(aggregateGroupBy.getCluster(), pilosaTraits, input, pilosaAggGroupByBitSet,
                ImmutableList.of(pilosaAggGroupByBitSet), aggregateGroupBy.getAggCallList());
    }

    private PilosaAggregate convertEnumCountingAggregateIntoPilosaCountingAggregate(Aggregate aggregateCount, PilosaAggregate pilosaAggregateGroupBy) {
        ImmutableBitSet pilosaAggCountBitSet = ImmutableBitSet.of();
        RelTraitSet pilosaTraits = aggregateCount.getTraitSet().replace(PilosaRel.CONVENTION);
        return new PilosaAggregate(pilosaAggregateGroupBy.getCluster(), pilosaTraits, pilosaAggregateGroupBy, pilosaAggCountBitSet,
                ImmutableList.of(pilosaAggCountBitSet), aggregateCount.getAggCallList());
    }

    private PilosaToEnumerableConverter linkPilosaEnumConverterWithCountingAggregate(PilosaToEnumerableConverter pilosaToEnumerableConverter, PilosaAggregate pilosaCountingAggregate) {
        return (PilosaToEnumerableConverter) pilosaToEnumerableConverter.copy(
                pilosaToEnumerableConverter.getTraitSet(), Collections.singletonList(pilosaCountingAggregate));
    }

    private EnumerableCorrelate linkCorrelateWithNewPilosaEnumConverter(Correlate correlate, JdbcToEnumerableConverter jdbcToEnumerableConverter, PilosaToEnumerableConverter pilosaEnumConverterWithCountingAggregate) {
        return new EnumerableCorrelate(correlate.getCluster(), correlate.getTraitSet(), jdbcToEnumerableConverter,
                    pilosaEnumConverterWithCountingAggregate, correlate.getCorrelationId(), correlate.getRequiredColumns(), correlate.getJoinType());
    }

    private EnumerableProject createEnumerableProject(Aggregate countingAggregate, EnumerableCorrelate correlate) {
        RelDataType resultRowType = countingAggregate.getRowType();
        List<RexInputRef> refs = prepareRexInputRefs(correlate.getRowType(), resultRowType);
        if (!checkPlanCorrectness(resultRowType, refs)) {
            throw new WrongPlanError();
        }
        return new EnumerableProject(countingAggregate.getCluster(), countingAggregate.getTraitSet(), correlate, refs, resultRowType);
    }

    private List<RexInputRef> prepareRexInputRefs(RelDataType inputType, RelDataType outputType) {
        List<Integer> groupingIndexes = deriveGroupingIndexesFromInputAndOutputRows(inputType, outputType);

        List<RelDataTypeField> inputTypeFieldList = inputType.getFieldList();
        return groupingIndexes.stream().map(i -> {
            RelDataTypeField inputField = inputTypeFieldList.get(i);
            RelDataType fieldType = inputField.getType();
            return new RexInputRef(i, fieldType);
        }).collect(Collectors.toList());
    }

    /*
    For some reason Calcite messes Jdbc and Pilosa table sometimes.
    We make sure that PilosaToEnumerableConverter has ID field, otherwise it is in JdbcToEnumerableConverter, which is wrong.
    We can only check that plan is correct by observing incompatibility between result type and inputs.
    */
    private boolean checkPlanCorrectness(RelDataType resultRowType, List<RexInputRef> refs) {
        List<RexNode> nodes = ImmutableList.copyOf(refs);
        return RexUtil.compatibleTypes(nodes, resultRowType, Litmus.IGNORE);
    }


}
