package com.alexrnv.calcite.adapter.pilosa.pilosa.converter;

import com.alexrnv.calcite.adapter.pilosa.pilosa.PilosaAPIError;
import com.alexrnv.calcite.adapter.pilosa.pilosa.dto.PilosaQueryResult;
import com.alexrnv.calcite.adapter.pilosa.pilosa.query.PilosaQuery;

import static com.alexrnv.calcite.adapter.pilosa.pilosa.PilosaAPIError.ErrorCode.INVALID_REQUEST;

public class ConvertersFactory {

    public QueryResponseConverter<? extends PilosaQueryResult> getQueryResponseConverter(PilosaQuery pilosaQuery) {
        switch (pilosaQuery.getType()) {
            case COUNT:
                return new CountResponseConverter();
            case GROUP_BY:
                return new GroupByResponseConverter();
            default:
                throw new PilosaAPIError(INVALID_REQUEST, "unknown query type for: " + pilosaQuery);
        }
    }

    //TODO: make schema request (and others) a part of PilosaQuery API and get rid of dual logic here, should only
    //be one getResponseConverter() method
    public SchemaResponseConverter getSchemaResponseConverter() {
        return new SchemaResponseConverter();
    }
}
