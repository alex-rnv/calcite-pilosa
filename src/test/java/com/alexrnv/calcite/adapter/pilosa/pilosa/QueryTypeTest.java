package com.alexrnv.calcite.adapter.pilosa.pilosa;

import com.alexrnv.calcite.adapter.pilosa.pilosa.query.QueryType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class QueryTypeTest {

    private final List<String> COUNT_QUERIES = Arrays.asList(
            "Count()",
            "count()",
            "Count(Row(quantity=\"2147427245\"))",
            "Count(Union(Row(quantity=\"2147427245\"),Row(quantity=\"0\")))",
            "Count(Intersect(Row(quantity=\"2147422743\"),Union(Row(quantity=\"2147422742\"),Row(quantity=\"2147422744\"))))",
            "Count(Union(Intersect(Row(quantity=\"2147427245\"),Row(shop_id=\"1\")),Row(price_bucket=\"2\"),Row(language=\"2\")))"
    );

    private final List<String> GROUP_BY_QUERIES = Arrays.asList(
            "GroupBy()",
            "groupBy()",
            "groupby()",
            "GroupBy(Rows(shop_id),filter=Row(item_id=\"2\"))",
            "GroupBy(Rows(language),Rows(shop_id),filter=Union(Row(quantity=\"2147424192\"),Row(quantity=\"2147430396\")))",
            "GroupBy(Rows(customer_id),Rows(shop_id),Rows(price_bucket),filter=Row(item_id=\"2\"))"
    );

    private final List<String> UNKNOWN_QUERIES = Arrays.asList(
            null,
            "",
            "Min()",
            "Max()",
            "Sum()"
    );

    @Test
    public void testStartWithCount() {
        for (String query : COUNT_QUERIES) {
            QueryType type = QueryType.fromRawQuery(query);
            Assert.assertEquals(query, QueryType.COUNT, type);
        }
    }

    @Test
    public void testStartWithGroupBy() {
        for (String query : GROUP_BY_QUERIES) {
            QueryType type = QueryType.fromRawQuery(query);
            Assert.assertEquals(query, QueryType.GROUP_BY, type);
        }
    }

    @Test
    public void testUnknownQueries() {
        for (String query : UNKNOWN_QUERIES) {
            QueryType type = QueryType.fromRawQuery(query);
            Assert.assertEquals(query, QueryType.UNKNOWN, type);
        }
    }

}