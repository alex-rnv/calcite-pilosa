package com.alexrnv.calcite.adapter.pilosa.pilosa.converter;

import com.alexrnv.calcite.adapter.pilosa.pilosa.dto.CountQueryResult;

import java.util.List;
import java.util.stream.Collectors;

public class CountResponseConverter extends QueryResponseConverter<CountQueryResult> {
    @Override
    public List<Object> convert(CountQueryResult queryResult) {
        return queryResult.getResults().stream().map(r -> (Object)r).collect(Collectors.toList());
    }
}
