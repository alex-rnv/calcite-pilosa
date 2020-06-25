package com.alexrnv.calcite.adapter.pilosa.pilosa.query;

import com.alexrnv.calcite.adapter.pilosa.pilosa.PilosaAPIError;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.alexrnv.calcite.adapter.pilosa.pilosa.PilosaAPIError.ErrorCode.INVALID_REQUEST;

class PilosaQueryParser {

    private final String indexName;

    PilosaQueryParser(String indexName) {
        this.indexName = indexName;
    }

    PilosaQuery fromString(String queryString) {
        assertEmptyString(queryString);
        String[] parts = queryString.split(" ");
        boolean isBulk = isBulk(parts);
        List<QueryType> queryTypes = getQueryTypes(parts);
        QueryType queryType = assertQueryTypes(queryTypes);
        return new PilosaQuery(indexName, queryType, queryString, isBulk);
    }

    private void assertEmptyString(String queryString) {
        if (StringUtils.isEmpty(queryString)) {
            throw new PilosaAPIError(INVALID_REQUEST, "empty query string");
        }
    }

    private boolean isBulk(String[] parts) {
        return parts.length > 1;
    }

    private List<QueryType> getQueryTypes(String[] parts) {
        return Arrays.stream(parts)
                .map(QueryType::fromRawQuery)
                .collect(Collectors.toList());
    }

    private QueryType assertQueryTypes(List<QueryType> queryTypes) {
        if (queryTypes.size() == 1) {
            return queryTypes.get(0);
        }
        List<QueryType> distinct = queryTypes.stream().distinct().collect(Collectors.toList());
        if(distinct.size() > 1) {
            String msg = String.format("bulk queries with different types are not supported, found: %s", queryTypes.toString());
            throw new PilosaAPIError(INVALID_REQUEST, msg);
        } else if (distinct.contains(QueryType.GROUP_BY)) {
            String msg = "bulk GroupBy queries are not supported";
            throw new PilosaAPIError(INVALID_REQUEST, msg);
        }
        return distinct.get(0);
    }
}
