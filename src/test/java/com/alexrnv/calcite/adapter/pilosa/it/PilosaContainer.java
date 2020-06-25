package com.alexrnv.calcite.adapter.pilosa.it;

import org.testcontainers.containers.GenericContainer;

public class PilosaContainer extends GenericContainer {

    static final int PILOSA_API_PORT = 10101;

    public PilosaContainer() {
        super("pilosa/pilosa:latest");
        withExposedPorts(PILOSA_API_PORT);
    }
}
