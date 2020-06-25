package com.alexrnv.calcite.adapter.pilosa.model;

import org.apache.calcite.rex.RexFieldAccess;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CorrelationFieldsHolder {

    private Map<String, RexFieldAccess> params = new HashMap<>();

    public void put(String name, RexFieldAccess fieldAccess) {
        params.put(name, fieldAccess);
    }

    public Set<String> getAllParamNames() {
        return params.keySet();
    }

    public RexFieldAccess get(String name) {
        return params.get(name);
    }
}
