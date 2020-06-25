package com.alexrnv.calcite.adapter.pilosa.model.rules;

import com.alexrnv.calcite.adapter.pilosa.model.PilosaToEnumerableConverter;
import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaRel;
import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.core.RelFactories;
import org.apache.calcite.tools.RelBuilderFactory;

import java.util.function.Predicate;

public class PilosaToEnumerableConverterRule extends ConverterRule {
    public static final ConverterRule INSTANCE =
            new PilosaToEnumerableConverterRule(RelFactories.LOGICAL_BUILDER);


    public <R extends RelNode> PilosaToEnumerableConverterRule(RelBuilderFactory relBuilderFactory) {
        super(RelNode.class, (Predicate<RelNode>) r -> true, PilosaRel.CONVENTION,
                EnumerableConvention.INSTANCE, relBuilderFactory,
                "PilosaToEnumerableConverterRule");
    }

    @Override
    public RelNode convert(RelNode rel) {
        RelTraitSet newTraitSet = rel.getTraitSet().replace(getOutConvention());
        return new PilosaToEnumerableConverter(rel.getCluster(), newTraitSet, rel);
    }
}
