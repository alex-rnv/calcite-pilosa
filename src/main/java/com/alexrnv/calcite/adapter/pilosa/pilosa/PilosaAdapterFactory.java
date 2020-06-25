package com.alexrnv.calcite.adapter.pilosa.pilosa;

import com.alexrnv.calcite.adapter.pilosa.pilosa.client.PilosaClient;
import com.alexrnv.calcite.adapter.pilosa.pilosa.converter.ConvertersFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PilosaAdapterFactory {

    private final static Logger LOG = LoggerFactory.getLogger(PilosaAdapterFactory.class);

    public PilosaAdapter createAdapter(String pilosaUrl) {
        LOG.info("Initializing pilosa adapter with {}", pilosaUrl);
        PilosaClient client = PilosaClient.create(pilosaUrl);
        ConvertersFactory factory = new ConvertersFactory();
        return new PilosaAdapter(client, factory);
    }
}
