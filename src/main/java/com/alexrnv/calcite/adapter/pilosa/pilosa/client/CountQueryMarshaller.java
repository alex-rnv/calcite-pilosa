package com.alexrnv.calcite.adapter.pilosa.pilosa.client;

import com.alexrnv.calcite.adapter.pilosa.pilosa.dto.CountQueryResult;
import com.alexrnv.calcite.adapter.pilosa.pilosa.PilosaAPIError;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

import static com.alexrnv.calcite.adapter.pilosa.pilosa.PilosaAPIError.ErrorCode.SERVER_ERROR;

class CountQueryMarshaller implements QueryMarshaller<CountQueryResult>{

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public CountQueryResult readFromStream(InputStream stream) {
        try {
            return objectMapper.readValue(stream, CountQueryResult.class);
        } catch (IOException e) {
            throw new PilosaAPIError(SERVER_ERROR, e);
        }
    }
}
