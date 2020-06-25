package com.alexrnv.calcite.adapter.pilosa.pilosa;

import com.alexrnv.calcite.adapter.pilosa.pilosa.query.QueryType;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class PilosaAdapterTest {

    private static final String TEST_GROUP_BY_RESPONSE = "{\n" +
            "  \"results\": [\n" +
            "    [\n" +
            "      {\n" +
            "        \"count\": 23054,\n" +
            "        \"group\": [\n" +
            "          {\n" +
            "            \"field\": \"shop_id\",\n" +
            "            \"rowID\": \"gc9gdbv\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"field\": \"customer_id\",\n" +
            "            \"rowID\": \"abc1234\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"field\": \"quantity\",\n" +
            "            \"rowID\": \"12345678\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"field\": \"language\",\n" +
            "            \"rowID\": \"1\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"count\": 5,\n" +
            "        \"group\": [\n" +
            "          {\n" +
            "            \"field\": \"shop_id\",\n" +
            "            \"rowID\": \"gc9gdbv\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"field\": \"customer_id\",\n" +
            "            \"rowID\": \"xyz1234\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"field\": \"quantity\",\n" +
            "            \"rowID\": \"24680864\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"field\": \"language\",\n" +
            "            \"rowID\": \"2\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"count\": 16405,\n" +
            "        \"group\": [\n" +
            "          {\n" +
            "            \"field\": \"shop_id\",\n" +
            "            \"rowID\": \"qwert123\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"field\": \"customer_id\",\n" +
            "            \"rowID\": \"xyz1234\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"field\": \"quantity\",\n" +
            "            \"rowID\": \"24622864\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"field\": \"language\",\n" +
            "            \"rowID\": \"3\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    ]\n" +
            "  ]\n" +
            "}";


    @Test
    public void testSingleCount() {
        new PilosaAdapterAssert()
                .withPilosaClientResponse("{\"results\":[21290042]}")
                .expectAdapterResponse(Collections.singletonList(21290042L));

    }

    @Test
    public void testDoubleCount() {
        new PilosaAdapterAssert()
                .withPilosaClientResponse("{\"results\":[21290042, 18290073]}")
                .expectAdapterResponse(Arrays.asList(21290042L, 18290073L));
    }

    @Test
    public void testGroupByWithCorrectHeader() {
        List<Object> expected = new ArrayList<Object>() {{
            add(new Object[]{"abc1234", "gc9gdbv", "12345678", "1", 23054L});
            add(new Object[]{"xyz1234", "gc9gdbv", "24680864", "2", 5L});
            add(new Object[]{"xyz1234", "qwert123", "24622864", "3", 16405L});
        }};

        new PilosaAdapterAssert()
                .withPilosaClientResponse(TEST_GROUP_BY_RESPONSE)
                .withHeader(Arrays.asList("customer_id", "shop_id", "quantity", "language", "count"))
                .withType(QueryType.GROUP_BY)
                .expectAdapterResponse(expected);
    }

    @Test(expected = PilosaAPIError.class)
    public void testGroupByEmptyHeader() {
        new PilosaAdapterAssert()
                .withPilosaClientResponse(TEST_GROUP_BY_RESPONSE)
                .withType(QueryType.GROUP_BY)
                .expectAdapterResponse(null);

    }

    @Test(expected = PilosaAPIError.class)
    public void testGroupByIncorrectHeader() {
        new PilosaAdapterAssert()
                .withPilosaClientResponse(TEST_GROUP_BY_RESPONSE)
                .withType(QueryType.GROUP_BY)
                .withHeader(Arrays.asList("stargazer", "browser", "quantity", "language", "count"))
                .expectAdapterResponse(null);
    }

    private static class PilosaAdapterAssert {

        private PilosaAdapter adapter;
        private List<String> header = Collections.emptyList();
        private QueryType type = QueryType.COUNT;

        private PilosaAdapterAssert withPilosaClientResponse(String pilosaClientResponseJson) {
            this.adapter = new StubPilosaAdapter(pilosaClientResponseJson);
            return this;
        }

        private PilosaAdapterAssert withHeader(List<String> header) {
            this.header = header;
            return this;
        }

        private PilosaAdapterAssert withType(QueryType type) {
            this.type = type;
            return this;
        }

        private void expectAdapterResponse(List<Object> adapterResponse) {
            List<Object> result = this.adapter.queryPilosa("main", type.getStringValue(), header);
            assertListsOfArraysEqual(adapterResponse, result);
        }

        private void assertListsOfArraysEqual(List<Object> expected, List<Object> actual) {
            Assert.assertTrue(expected != null && actual != null);
            assertEquals(expected.size(), actual.size());

            if (!expected.isEmpty()) {

                for (int k = 0; k < expected.size(); k++) {
                    if (expected.get(k) instanceof Object[] && actual.get(k) instanceof Object[]) {
                        Object[] e = (Object[]) expected.get(k);
                        Object[] a = (Object[]) actual.get(k);
                        assertArrayEquals(e, a);
                    } else {
                        assertEquals(expected, actual);
                    }
                }

            }
        }
    }

}