package com.alexrnv.calcite.adapter.pilosa.pilosa.client;

import com.alexrnv.calcite.adapter.pilosa.pilosa.PilosaAPIError;
import com.alexrnv.calcite.adapter.pilosa.pilosa.dto.PilosaQueryResult;
import com.alexrnv.calcite.adapter.pilosa.pilosa.query.PilosaQuery;

import static com.alexrnv.calcite.adapter.pilosa.pilosa.PilosaAPIError.ErrorCode.INVALID_REQUEST;


class MarshallingFactory {

    private final CountQueryMarshaller countQueryMarshaller = new CountQueryMarshaller();
    private final GroupByQueryMarshaller groupByQueryMarshaller = new GroupByQueryMarshaller();
    private final SchemaQueryMarshaller schemaQueryMarshaller = new SchemaQueryMarshaller();

    @SuppressWarnings("unchecked")
    <T extends QueryMarshaller<? extends PilosaQueryResult>> T createForReadQuery(PilosaQuery pilosaQuery) {
        switch (pilosaQuery.getType()) {
            case COUNT:
                return (T)countQueryMarshaller;
            case GROUP_BY:
                return (T)groupByQueryMarshaller;
            default:
                throw new PilosaAPIError(INVALID_REQUEST, "can't detect marshaller from query: " + pilosaQuery);
        }
    }

    SchemaQueryMarshaller createForSchemaQuery() {
        return schemaQueryMarshaller;
    }

}
