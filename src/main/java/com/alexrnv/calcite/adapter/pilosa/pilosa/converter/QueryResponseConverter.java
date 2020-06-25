package com.alexrnv.calcite.adapter.pilosa.pilosa.converter;

import com.alexrnv.calcite.adapter.pilosa.pilosa.dto.PilosaQueryResult;

import java.util.List;

/**
 * Converts Pilosa API results into Calcite-readable objects.
 * @param <T> subtype of {@link PilosaQueryResult}
 */
public abstract class QueryResponseConverter<T extends PilosaQueryResult> {
    List<String> header;

    public void setHeader(List<String> header) {
        this.header = header;
    }

    public abstract List<Object> convert(T queryResult);

}
