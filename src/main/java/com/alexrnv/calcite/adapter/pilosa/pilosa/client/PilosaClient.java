package com.alexrnv.calcite.adapter.pilosa.pilosa.client;

import com.alexrnv.calcite.adapter.pilosa.pilosa.dto.PilosaQueryResult;
import com.alexrnv.calcite.adapter.pilosa.pilosa.PilosaAPIError;
import com.alexrnv.calcite.adapter.pilosa.pilosa.dto.SchemaQueryResult;
import com.alexrnv.calcite.adapter.pilosa.pilosa.query.PilosaQuery;
import org.apache.calcite.runtime.HttpUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.alexrnv.calcite.adapter.pilosa.pilosa.PilosaAPIError.ErrorCode.SERVER_ERROR;

public class PilosaClient {
    private final static Logger LOG = LoggerFactory.getLogger(PilosaClient.class);

    private static final int CONNECTION_TIMEOUT_MS = 10_000;
    private static final int READ_TIMEOUT_MS = 0; //infinite

    private static final String READ_QUERY_URI_FORMAT = "/index/%s/query";
    private static final String SCHEMA_QUERY_URI = "/schema";

    private final String url;
    private final MarshallingFactory marshallingFactory;

    public static PilosaClient create(String url) {
        return new PilosaClient(url, new MarshallingFactory());
    }

    PilosaClient(String url, MarshallingFactory marshallingFactory) {
        this.marshallingFactory = marshallingFactory;
        System.setProperty("http.keepAlive", "true");
        this.url = url;
    }

    public PilosaQueryResult executeQuery(PilosaQuery query) {
        String queryString = query.getQueryString();
        String queryUrl = StringUtils.removeEnd(this.url, "/") + String.format(READ_QUERY_URI_FORMAT, query.getIndexName());
        try (InputStream stream = post(queryUrl, queryString)) {
            return marshallingFactory.createForReadQuery(query).readFromStream(stream);
        } catch (IOException e) {
            LOG.error("query failed to execute", e);
            throw new PilosaAPIError(SERVER_ERROR, e);
        }
    }

    public SchemaQueryResult getSchema() {
        String queryUrl = StringUtils.removeEnd(this.url, "/") + SCHEMA_QUERY_URI;
        try (InputStream stream = post(queryUrl)) {
            return marshallingFactory.createForSchemaQuery().readFromStream(stream);
        } catch (IOException e) {
            LOG.error("query failed to execute", e);
            throw new PilosaAPIError(SERVER_ERROR, e);
        }
    }

    InputStream post(String url) throws IOException {
        String logQueryID = RandomStringUtils.randomAlphanumeric(8);
        LOG.info("QueryID={}: Url='{}'", logQueryID, url);
        Map<String, String> headers = new HashMap<>();
        headers.put("Connection", "close");
        headers.put("Accept", "*/*");
        long t = System.currentTimeMillis();
        try {
            return HttpUtils.post(url, null, headers, CONNECTION_TIMEOUT_MS, READ_TIMEOUT_MS);
        } finally {
            LOG.info("QueryID={}: took {}ms", logQueryID, (System.currentTimeMillis() - t));
        }
    }

    InputStream post(String url, String pilosaQueryStr) throws IOException {
        String logQueryID = RandomStringUtils.randomAlphanumeric(8);
        LOG.info("QueryID={}: Url='{}'; Query={}", logQueryID, url, pilosaQueryStr);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Connection", "close");
        headers.put("Accept", "*/*");
        long t = System.currentTimeMillis();
        try {
            return HttpUtils.post(url, pilosaQueryStr, headers, CONNECTION_TIMEOUT_MS, READ_TIMEOUT_MS);
        } finally {
            LOG.info("QueryID={}: took {}ms", logQueryID, (System.currentTimeMillis() - t));
        }
    }

}

