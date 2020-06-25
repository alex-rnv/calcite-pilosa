package com.alexrnv.calcite.adapter.pilosa.model.rules;

import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaProject;
import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaRel;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.logical.LogicalProject;

class PilosaProjectRule extends ConverterRule {
    final static PilosaProjectRule INSTANCE = new PilosaProjectRule();

    private PilosaProjectRule() {
        super(LogicalProject.class, Convention.NONE, PilosaRel.CONVENTION, "PilosaProjectRule");
    }

    @Override
    public RelNode convert(RelNode rel) {
        final LogicalProject project = (LogicalProject) rel;
        final RelTraitSet traitSet = project.getTraitSet().replace(PilosaRel.CONVENTION);
        return new PilosaProject(project.getCluster(), traitSet,
                convert(project.getInput(), PilosaRel.CONVENTION), project.getProjects(),
                project.getRowType());
    }
}
