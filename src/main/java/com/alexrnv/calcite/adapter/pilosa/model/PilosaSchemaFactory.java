package com.alexrnv.calcite.adapter.pilosa.model;

import com.alexrnv.calcite.adapter.pilosa.pilosa.PilosaAdapter;
import com.alexrnv.calcite.adapter.pilosa.pilosa.PilosaAdapterFactory;
import com.alexrnv.calcite.adapter.pilosa.pilosa.PilosaTableDefinition;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaFactory;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PilosaSchemaFactory implements SchemaFactory {

    private final PilosaAdapterFactory pilosaAdapterFactory = new PilosaAdapterFactory();

    @Override
    public Schema create(SchemaPlus parentSchema, String name, Map<String, Object> operand) {
        String url = (String) operand.get("url");
        PilosaAdapter adapter = pilosaAdapterFactory.createAdapter(url);
        List<PilosaTableDefinition> tableDefinitions = adapter.getSchema();
        Map<String, Table> calciteTables = convertToCalciteTables(tableDefinitions, adapter);
        return new PilosaSchema(calciteTables);
    }

    private Map<String, Table> convertToCalciteTables(List<PilosaTableDefinition> tableDefinitions, PilosaAdapter adapter) {
        return tableDefinitions.stream()
                .collect(Collectors.toMap(
                        PilosaTableDefinition::getName,
                        td -> new PilosaTable(adapter, td.getName(), td.getColumnNames(), td.getJavaTypes())));
    }
}
