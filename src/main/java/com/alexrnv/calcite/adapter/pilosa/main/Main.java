package com.alexrnv.calcite.adapter.pilosa.main;

import com.alexrnv.calcite.adapter.pilosa.module.PilosaCommandLine;
import com.alexrnv.calcite.adapter.pilosa.module.PilosaHttpServerFactory;
import com.alexrnv.calcite.adapter.pilosa.module.PilosaServiceFactory;
import org.apache.calcite.avatica.remote.LocalService;
import org.apache.calcite.avatica.server.HttpServer;
import org.apache.commons.cli.CommandLine;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        CommandLine cmd = PilosaCommandLine.init(args);
        String uri = cmd.getOptionValue(PilosaCommandLine.MODEL_URI_PARAM);
        checkFileExists(uri);
        LocalService service = new PilosaServiceFactory(uri).createLocalService();

        int port = parseInteger(cmd.getOptionValue(PilosaCommandLine.PORT_PARAM));
        HttpServer server = new PilosaHttpServerFactory(service, port).createHttpServer();
        registerGracefulStop(server);
        startAndListen(server);
    }

    private static void checkFileExists(String uri) {
        File f = new File(uri);
        if (!f.exists()) {
            throw new RuntimeException("Model " + uri + " does not exist");
        }
    }

    private static int parseInteger(String port) {
        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid port specified: " + port);
        }
    }

    private static void registerGracefulStop(HttpServer server) {
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }

    private static void startAndListen(HttpServer server) {
        server.start();
        try {
            server.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
