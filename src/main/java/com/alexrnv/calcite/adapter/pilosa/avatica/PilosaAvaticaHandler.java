package com.alexrnv.calcite.adapter.pilosa.avatica;

import org.apache.calcite.avatica.remote.Service;
import org.apache.calcite.avatica.server.AvaticaJsonHandler;
import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class PilosaAvaticaHandler extends AvaticaJsonHandler {
    private static final Logger LOG = LoggerFactory.getLogger(PilosaAvaticaHandler.class);

    public static final String AVATICA_PATH = "/sql/v1";
    private AtomicLong requestCounter = new AtomicLong();

    public PilosaAvaticaHandler(Service service) {
        super(service);
    }

    @Override
    public void handle(
            final String target,
            final Request baseRequest,
            final HttpServletRequest request,
            final HttpServletResponse response
    ) throws IOException, ServletException
    {
        if (request.getRequestURI().equals(AVATICA_PATH)) {
            long start = System.currentTimeMillis();
            long number = requestCounter.getAndIncrement();
            LOG.debug("received request #{}", number);
            super.handle(target, baseRequest, request, response);
            LOG.debug("processed request #{} in {}ms", number, (System.currentTimeMillis() - start));
        }
    }
}
