package com.alexrnv.calcite.adapter.pilosa.pilosa.client;

import com.alexrnv.calcite.adapter.pilosa.pilosa.PilosaAPIError;
import com.alexrnv.calcite.adapter.pilosa.pilosa.dto.SchemaQueryResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

import static com.alexrnv.calcite.adapter.pilosa.pilosa.PilosaAPIError.ErrorCode.SERVER_ERROR;

public class SchemaQueryMarshaller implements QueryMarshaller<SchemaQueryResult> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public SchemaQueryResult readFromStream(InputStream stream) {
        try {
            return objectMapper.readValue(stream, SchemaQueryResult.class);
        } catch (IOException e) {
            throw new PilosaAPIError(SERVER_ERROR, e);
        }
    }
}
