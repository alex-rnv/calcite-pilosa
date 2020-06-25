package com.alexrnv.calcite.adapter.pilosa.model;

class TestFixtures {

    static final int AVATICA_SERVER_PORT = 8484;

    static final String STUB_PILOSA_MODEL = "{\n" +
            "  \"version\": \"1.0\",\n" +
            "  \"defaultSchema\": \"pilosa\",\n" +
            "  \"schemas\": [\n" +
            "    {\n" +
            "      \"name\": \"custom\",\n" +
            "      \"tables\": [\n" +
            "        {\n" +
            "          \"name\": \"main\",\n" +
            "          \"type\": \"custom\",\n" +
            "          \"factory\": \"com.alexrnv.calcite.adapter.pilosa.model.StubPilosaTableFactory\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    static final String STUB_PILOSA_AND_JDBC_MODEL = "{\n" +
            "  \"version\": \"1.0\",\n" +
            "  \"defaultSchema\": \"pilosa\",\n" +
            "  \"schemas\": [\n" +
            "    {\n" +
            "      \"name\": \"custom\",\n" +
            "      \"tables\": [\n" +
            "        {\n" +
            "          \"name\": \"main\",\n" +
            "          \"type\": \"custom\",\n" +
            "          \"factory\": \"com.alexrnv.calcite.adapter.pilosa.model.StubPilosaTableFactory\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"jdbc\",\n" +
            "      \"type\": \"custom\",\n" +
            "      \"factory\": \"org.apache.calcite.adapter.jdbc.JdbcSchema$Factory\",\n" +
            "      \"operand\": {\n" +
            "        \"jdbcDriver\": \"org.h2.Driver\",\n" +
            "        \"jdbcUrl\": \"<URL_PLACEHOLDER>\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    static final String STUB_PILOSA_MODEL_URI_INLINE = "inline:" + STUB_PILOSA_MODEL;

    static final String SAMPLE_QUERY = "" +
            "select " +
            "count(distinct purchase_id) as volume " +
            "from custom.main \n" +
            "where\n" +
            "(quantity = '2147427245' and item_id = '1')\n" +
            "or\n" +
            "(quantity = '2147427246' and item_id = '10')";

}
