package com.alexrnv.calcite.adapter.pilosa.model;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.TableFactory;

import java.util.Map;

public class PilosaTableFactory implements TableFactory<PilosaTable> {

    @Override
    public PilosaTable create(SchemaPlus schema, String name, Map<String, Object> operand, RelDataType rowType) {
        throw new RuntimeException("not implemented"); //TBD
    }
}
