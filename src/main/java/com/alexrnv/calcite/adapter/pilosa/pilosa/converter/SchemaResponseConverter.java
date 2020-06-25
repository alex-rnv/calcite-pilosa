package com.alexrnv.calcite.adapter.pilosa.pilosa.converter;

import com.alexrnv.calcite.adapter.pilosa.pilosa.PilosaTableDefinition;
import com.alexrnv.calcite.adapter.pilosa.pilosa.dto.Field;
import com.alexrnv.calcite.adapter.pilosa.pilosa.dto.FieldType;
import com.alexrnv.calcite.adapter.pilosa.pilosa.dto.Index;
import com.alexrnv.calcite.adapter.pilosa.pilosa.dto.SchemaQueryResult;

import java.util.*;
import java.util.stream.Collectors;

public class SchemaResponseConverter {

    private static final EnumMap<FieldType, Class<?>> PILOSA_TYPE_TO_JAVA_CLASS_MAP = new EnumMap<FieldType, Class<?>>(FieldType.class) {{
        put(FieldType.SET, Long.class);
        put(FieldType.INT, Long.class);
        put(FieldType.BOOL, Boolean.class);
        put(FieldType.TIME, String.class); ///!!!!!???????
        put(FieldType.MUTEX, Boolean.class); ///!!!!!???????
    }};

    public List<PilosaTableDefinition> convert(SchemaQueryResult schema) {
        return schema.getIndexes().stream()
                .map(this::mapIndex)
                .collect(Collectors.toList());
    }

    private PilosaTableDefinition mapIndex(Index index) {
        String indexName = index.getName();
        List<Field> fields = index.getFields();
        List<String> columnNames = new ArrayList<>(fields.size());
        List<Class<?>> javaTypes = new ArrayList<>(fields.size());

        fields.forEach(field -> {
            String name = field.getName();
            FieldType type = field.getOptions().getType();
            Class<?> javaType = PILOSA_TYPE_TO_JAVA_CLASS_MAP.get(type);
            columnNames.add(name);
            javaTypes.add(javaType);
        });
        return new PilosaTableDefinition(indexName, columnNames, javaTypes);
    }


}
