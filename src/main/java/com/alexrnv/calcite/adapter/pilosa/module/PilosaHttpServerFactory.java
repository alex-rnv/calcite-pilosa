package com.alexrnv.calcite.adapter.pilosa.module;

import com.alexrnv.calcite.adapter.pilosa.avatica.ConfigurableHttpServer;
import com.alexrnv.calcite.adapter.pilosa.avatica.PilosaAvaticaHandler;
import org.apache.calcite.avatica.remote.LocalService;
import org.apache.calcite.avatica.server.HttpServer;

public class PilosaHttpServerFactory {

    private final LocalService localService;
    private final int port;

    public PilosaHttpServerFactory(LocalService localService, int port) {
        this.localService = localService;
        this.port = port;
    }

    public HttpServer createHttpServer() {
        return new ConfigurableHttpServer(port, new PilosaAvaticaHandler(localService), 30 * 60 * 1000);
    }
}
