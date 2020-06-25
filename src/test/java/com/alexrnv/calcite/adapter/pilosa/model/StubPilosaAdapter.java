package com.alexrnv.calcite.adapter.pilosa.model;

import com.alexrnv.calcite.adapter.pilosa.pilosa.PilosaAdapter;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Returns raw Pilosa query instead of results
 */
public class StubPilosaAdapter extends PilosaAdapter {

    static final String ULTIMATE_UNIVERSAL_RESPONSE_STRING = "42";
    static final long ULTIMATE_UNIVERSAL_RESPONSE_LONG = 42L;

    static final StubPilosaAdapter INSTANCE = new StubPilosaAdapter();

    private Function<String, String> callback;

    private StubPilosaAdapter() {
        super(null, null);
    }

    void setCallback(Function<String, String> callback) {
        this.callback = callback;
    }

    @Override
    public List<Object> queryPilosa(String tableName, String pilosaQueryStr, List<String> header) {
        if (callback != null) {
            callback.apply(pilosaQueryStr);
        }
        return createResponseRow(header);
    }

    private List<Object> createResponseRow(List<String> header) {
        if (header.size() == 1) {
            return Collections.singletonList(ULTIMATE_UNIVERSAL_RESPONSE_LONG);
        }
        Object[] row = header.stream().map(col -> {
            if (col.startsWith("$") || col.equalsIgnoreCase("VOLUME")) {
                return ULTIMATE_UNIVERSAL_RESPONSE_LONG;
            } else {
                return ULTIMATE_UNIVERSAL_RESPONSE_STRING;
            }
        }).toArray();
        return Collections.singletonList(row);
    }
}
