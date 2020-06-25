package com.alexrnv.calcite.adapter.pilosa.pilosa;

import com.alexrnv.calcite.adapter.pilosa.pilosa.converter.ConvertersFactory;
import com.alexrnv.calcite.adapter.pilosa.pilosa.dto.CountQueryResult;
import com.alexrnv.calcite.adapter.pilosa.pilosa.dto.GroupByQueryResult;
import com.alexrnv.calcite.adapter.pilosa.pilosa.dto.PilosaQueryResult;
import com.alexrnv.calcite.adapter.pilosa.pilosa.query.PilosaQuery;
import com.alexrnv.calcite.adapter.pilosa.pilosa.query.QueryType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

class StubPilosaAdapter extends PilosaAdapter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String staticResponse;

    public StubPilosaAdapter(String staticResponse) {
        super(null, new ConvertersFactory());
        this.staticResponse = staticResponse;
    }

    @Override
    protected PilosaQueryResult execute(PilosaQuery query) {
        return null;
    }

    @Override
    protected List<Object> convertQueryResult(PilosaQuery query, PilosaQueryResult result, List<String> header) {
        try {
            return super.convertQueryResult(query, generateStaticResult(query.getType()), header);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private PilosaQueryResult generateStaticResult(QueryType type) throws IOException {
        switch (type) {
            case GROUP_BY:
                return objectMapper.readValue(staticResponse, GroupByQueryResult.class);
            case COUNT:
                return objectMapper.readValue(staticResponse, CountQueryResult.class);
            default:
                throw new AssertionError("wrong static response");
        }
    }
}
