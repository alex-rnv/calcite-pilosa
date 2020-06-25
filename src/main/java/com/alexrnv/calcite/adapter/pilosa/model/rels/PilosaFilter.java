package com.alexrnv.calcite.adapter.pilosa.model.rels;

import com.alexrnv.calcite.adapter.pilosa.model.CorrelationFieldsHolder;
import com.alexrnv.calcite.adapter.pilosa.model.PilosaRelTreeVisitor;
import com.alexrnv.calcite.adapter.pilosa.expression.PilosaFilterExpression;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Filter;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rex.*;

public class PilosaFilter extends Filter implements PilosaRel {

    public PilosaFilter(RelOptCluster cluster, RelTraitSet traits, RelNode child, RexNode condition) {
        super(cluster, traits, child, condition);
    }

    @Override
    public Filter copy(RelTraitSet traitSet, RelNode input, RexNode condition) {
        return new PilosaFilter(getCluster(), traitSet, input, condition);
    }

    @Override
    public void accept(PilosaRelTreeVisitor visitor) {
        visitChild(visitor);
        FilterTranslator translator = translate();
        enqueueFilterExpression(visitor, translator.getTranslation());
        storeCorrelationParams(translator, visitor.getCorrelationFieldsHolder());
    }

    private void enqueueFilterExpression(PilosaRelTreeVisitor visitor, String translation) {
        PilosaFilterExpression pilosaFilterExpression = new PilosaFilterExpression(translation);
        visitor.enqueueInReversePolishNotation(pilosaFilterExpression);
    }

    private void storeCorrelationParams(FilterTranslator translator, CorrelationFieldsHolder holder) {
        translator.addCorrelationParams(holder);
    }

    private FilterTranslator translate() {
        FilterTranslator translator = FilterTranslator.of(getRowType(), condition);
        translator.translate();
        return translator;
    }

    private void visitChild(PilosaRelTreeVisitor visitor) {
        visitor.visitChild(getInput());
    }

    @Override
    protected RelDataType deriveRowType() {
        return input.getRowType();
    }

}
