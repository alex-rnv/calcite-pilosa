package com.alexrnv.calcite.adapter.pilosa.model.rels;

import org.apache.calcite.plan.Convention;
import org.apache.calcite.rel.RelNode;

public interface PilosaRel extends PilosaElement, RelNode {

    Convention CONVENTION = new Convention.Impl("PILOSA", PilosaRel.class);

    @Override
    Convention getConvention();
}
