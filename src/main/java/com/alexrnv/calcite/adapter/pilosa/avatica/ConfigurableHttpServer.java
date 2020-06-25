package com.alexrnv.calcite.adapter.pilosa.avatica;

import org.apache.calcite.avatica.server.AvaticaHandler;
import org.apache.calcite.avatica.server.HttpServer;
import org.eclipse.jetty.server.ServerConnector;

public class ConfigurableHttpServer extends HttpServer {

    private final int idleTimeoutMs;

    public ConfigurableHttpServer(int port, AvaticaHandler handler, int idleTimeoutMs) {
        super(port, handler);
        this.idleTimeoutMs = idleTimeoutMs;
    }

    @Override
    protected ServerConnector configureConnector(ServerConnector connector, int port) {
        super.configureConnector(connector, port);
        connector.setIdleTimeout(idleTimeoutMs);
        return connector;
    }
}
