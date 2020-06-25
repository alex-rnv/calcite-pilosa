package com.alexrnv.calcite.adapter.pilosa.pilosa;

import com.alexrnv.calcite.adapter.pilosa.commons.Timer;
import com.alexrnv.calcite.adapter.pilosa.pilosa.client.PilosaClient;
import com.alexrnv.calcite.adapter.pilosa.pilosa.converter.QueryResponseConverter;
import com.alexrnv.calcite.adapter.pilosa.pilosa.converter.ConvertersFactory;
import com.alexrnv.calcite.adapter.pilosa.pilosa.converter.SchemaResponseConverter;
import com.alexrnv.calcite.adapter.pilosa.pilosa.dto.PilosaQueryResult;
import com.alexrnv.calcite.adapter.pilosa.pilosa.dto.SchemaQueryResult;
import com.alexrnv.calcite.adapter.pilosa.pilosa.query.PilosaQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PilosaAdapter {

    private final static Logger LOG = LoggerFactory.getLogger(PilosaAdapter.class);

    private final PilosaClient client;
    private final ConvertersFactory convertersFactory;

    public PilosaAdapter(PilosaClient client, ConvertersFactory convertersFactory) {
        this.client = client;
        this.convertersFactory = convertersFactory;
    }

    public List<PilosaTableDefinition> getSchema() {
        SchemaQueryResult result = client.getSchema();
        return convertSchemaQueryResult(result);
    }

    public List<Object> queryPilosa(String tableName, String pilosaQueryStr, List<String> header) {
        PilosaQuery query = PilosaQuery.fromString(tableName, pilosaQueryStr);
        PilosaQueryResult result = execute(query);
        return convertQueryResult(query, result, header);
    }

    protected PilosaQueryResult execute(PilosaQuery query) {
        Timer timer = Timer.start();
        LOG.info("QueryID={}: {}", query.getId(), query.getQueryString());
        PilosaQueryResult result = client.executeQuery(query);
        LOG.info("QueryID={}: done in {}ms", query.getId(), timer.elapsed());
        return result;
    }

    protected List<PilosaTableDefinition> convertSchemaQueryResult(SchemaQueryResult result) {
        SchemaResponseConverter converter = convertersFactory.getSchemaResponseConverter();
        return converter.convert(result);
    }

    @SuppressWarnings("unchecked")
    protected List<Object> convertQueryResult(PilosaQuery query, PilosaQueryResult result, List<String> header) {
        QueryResponseConverter converter = convertersFactory.getQueryResponseConverter(query);
        converter.setHeader(header);
        return converter.convert(result);
    }

}
