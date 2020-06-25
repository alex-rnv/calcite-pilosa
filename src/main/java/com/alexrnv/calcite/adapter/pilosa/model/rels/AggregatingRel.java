package com.alexrnv.calcite.adapter.pilosa.model.rels;

import org.apache.calcite.rel.core.AggregateCall;
import org.apache.calcite.rel.type.RelDataType;

import java.util.List;

interface AggregatingRel {
    List<AggregateCall> getAggCallList();
    List<Integer> getGroupByList();
    RelDataType getInputRowType();
}
