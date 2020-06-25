package com.alexrnv.calcite.adapter.pilosa.it;

import org.apache.calcite.runtime.HttpUtils;
import org.apache.calcite.test.CalciteAssert;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

public class PilosaContainerIT {

    private static final String PILOSA_CONTAINER_MODEL = "{\n" +
            "  \"version\": \"1.0\",\n" +
            "  \"defaultSchema\": \"pilosa\",\n" +
            "  \"schemas\": [\n" +
            "    {\n" +
            "      \"name\": \"pilosa\",\n" +
            "      \"type\": \"custom\",\n" +
            "      \"factory\": \"com.alexrnv.calcite.adapter.pilosa.model.PilosaSchemaFactory\",\n" +
            "      \"operand\": {\n" +
            "        \"url\": \"<PILOSA_URL>\"\n" +
            "      }\n" +
            "    }\n" +
            "    ]\n" +
            "}";

    @ClassRule
    public static PilosaContainer pilosa = new PilosaContainer();
    private static PilosaTestDBHelper pilosaTestDBHelper;

    @BeforeClass
    public static void setUp() {
        pilosaTestDBHelper = new PilosaTestDBBuilder(pilosa).setUpTestDB();
    }

    private InputStream post(String url, String data) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Connection", "close");
        headers.put("Accept", "*/*");
        return HttpUtils.post(url, data, headers);
    }

    //Sanity checking

    @Test
    public void testQueryRow() throws IOException {
        String queryUrl = pilosaTestDBHelper.getQueryEndpoint();
        String response = new String(IOUtils.toByteArray(post(queryUrl, "Row(stargazer=14)")));
        assertThatJson("{\n" +
                "    \"results\":[\n" +
                "        {\n" +
                "            \"attrs\":{},\n" +
                "            \"columns\":[1,2,3,362,368,391,396,409,416,430,436,450,454,460,461,464,466,469,470,483,484,486,490,491,503,504,514]\n" +
                "        }\n" +
                "    ]\n" +
                "}").isEqualTo(response);
    }

    @Test
    public void testQueryTopN() throws IOException {
        String queryUrl = pilosaTestDBHelper.getQueryEndpoint();
        String response = new String(IOUtils.toByteArray(post(queryUrl, "TopN(language, n=5)")));
        assertThatJson("{\n" +
                "    \"results\":[\n" +
                "        [\n" +
                "            {\"id\":5,\"count\":119},\n" +
                "            {\"id\":1,\"count\":50},\n" +
                "            {\"id\":4,\"count\":48},\n" +
                "            {\"id\":9,\"count\":31},\n" +
                "            {\"id\":13,\"count\":25}\n" +
                "        ]\n" +
                "    ]\n" +
                "}").isEqualTo(response);
    }

    //SQL testing

    @Test
    public void testCount_SQL() {
        CalciteAssert.that()
                .withModel(getPilosaContainerModel())
                .with("caseSensitive", "false")
                .query(
                        "select count(*) " +
                        "from pilosa.repository " +
                        "where " +
                        "stargazer=14"
                )
                .returnsValue("27");
    }

    @Test
    public void testUnion_SQL() {
        CalciteAssert.that()
                .withModel(getPilosaContainerModel())
                .with("caseSensitive", "false")
                .query(
                        "select count(*) " +
                                "from pilosa.repository " +
                                "where " +
                                "stargazer=14 or stargazer=19"
                )
                .returnsValue("50");
    }

    @Test
    public void testIntersect_SQL() {
        CalciteAssert.that()
                .withModel(getPilosaContainerModel())
                .with("caseSensitive", "false")
                .query(
                        "select count(distinct _id) as num " +
                                "from " +
                                "(" +
                                "select _id " +
                                "from pilosa.repository " +
                                "where " +
                                "stargazer = 14 " +

                                "intersect " +

                                "select _id " +
                                "from pilosa.repository " +
                                "where " +
                                "stargazer = 19 " +
                                ")"
                )
                .returnsValue("10");
    }

    private static String getPilosaContainerModel() {
        String queryUrl = pilosaTestDBHelper.getBaseEndpoint();
        return PILOSA_CONTAINER_MODEL.replace("<PILOSA_URL>", queryUrl);
    }

}
