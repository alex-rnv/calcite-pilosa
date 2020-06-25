package com.alexrnv.calcite.adapter.pilosa.module;

import com.alexrnv.calcite.adapter.pilosa.avatica.LocalServiceWrapper;
import org.apache.calcite.avatica.AvaticaConnection;
import org.apache.calcite.avatica.Meta;
import org.apache.calcite.avatica.remote.LocalService;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.model.ModelHandler;

import java.sql.Connection;
import java.util.Properties;

public class PilosaServiceFactory {

    private final String uri;

    public PilosaServiceFactory(String uri) {
        this.uri = uri;
    }

    public LocalService createLocalService() {
        Properties info = new Properties();
        info.put("caseSensitive", "false");
        info.put("model", uri);

        org.apache.calcite.jdbc.Driver driver = new org.apache.calcite.jdbc.Driver();
        try {
            Connection con = driver.connect("jdbc:calcite:", info);
            Meta meta = driver.createMeta((AvaticaConnection) con);

            //WA: a bit ugly way to link our json model to AvaticaConnection
            new ModelHandler((CalciteConnection) con, uri);

            return new LocalServiceWrapper(meta);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
