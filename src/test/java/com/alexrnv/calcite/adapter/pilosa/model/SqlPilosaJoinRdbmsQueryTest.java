package com.alexrnv.calcite.adapter.pilosa.model;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.zapodot.junit.db.CompatibilityMode;
import org.zapodot.junit.db.EmbeddedDatabaseRule;

import java.util.Arrays;
import java.util.Objects;

public class SqlPilosaJoinRdbmsQueryTest {

    private static String calciteModelUri;

    @ClassRule
    public static final EmbeddedDatabaseRule embeddedRDBMS = EmbeddedDatabaseRule
            .h2()
            .withMode(CompatibilityMode.PostgreSQL)
            .withInitialSqlFromResource(Objects.requireNonNull(SqlPilosaJoinRdbmsQueryTest.class.getClassLoader().getResource("metadata.sql")).getFile())
            .build();

    @BeforeClass
    public static void setUp() {
        String url = embeddedRDBMS.getConnectionJdbcUrl();
        calciteModelUri = TestFixtures.STUB_PILOSA_AND_JDBC_MODEL.replace("<URL_PLACEHOLDER>", url);
    }

    @Test
    public void testCountJoinOnItem() {
        new PilosaAssert()
                .withModel(calciteModelUri)
                .withSql(
                        "select count(distinct m.purchase_id) " +
                                "from " +
                                "custom.main m " +
                                "join " +
                                "jdbc.items i " +
                                "on " +
                                "m.item_id = cast(i.id as varchar) " +
                                "where " +
                                "i.name = 'Surfboard'"
                )
                .expectPilosaExpression("Count(Row(item_id=7))")
                .run();
    }

    @Test
    public void testCountJoinOnShop() {
        new PilosaAssert()
                .withModel(calciteModelUri)
                .withSql(
                        "select s.name, count(distinct m.purchase_id) " +
                                "from " +
                                "custom.main m " +
                                "join " +
                                "jdbc.shops s " +
                                "on " +
                                "m.shop_id = cast(s.id as varchar)" +
                                "where " +
                                "s.name = 'Alpha Market' " +
                                "group by s.name"
                )
                .expectPilosaExpression("Count(Row(shop_id=1))")
                .run();
    }

    @Test
    public void testCountJoinOnMultipleItems() {
        new PilosaAssert()
                .withModel(calciteModelUri)
                .withSql(
                        "select i.name, count(distinct m.purchase_id) " +
                                "from " +
                                "custom.main m " +
                                "join " +
                                "jdbc.items i " +
                                "on " +
                                "m.item_id = cast(i.id as varchar) " +
                                "where " +
                                "i.category = 1 " +
                                "group by i.name"
                )
                .expectPilosaExpressions("Count(Row(item_id=###))", "###", Arrays.asList("1", "4"))
                .run();
    }

    @Test
    public void testCountJoinOnMultipleItemsWithInStatement() {
        new PilosaAssert()
                .withModel(calciteModelUri)
                .withSql(
                        "select i.name, count(distinct m.purchase_id) " +
                                "from " +
                                "custom.main m " +
                                "join " +
                                "jdbc.items i " +
                                "on " +
                                "m.item_id = cast(i.id as varchar) " +
                                "where " +
                                "i.name in ('Chair', 'Table')" +
                                "group by i.name"
                )
                .expectPilosaExpressions("Count(Row(item_id=###))", "###", Arrays.asList("2", "5"))
                .run();
    }

    @Test
    public void testCountJoinOnShopsList() {
        new PilosaAssert()
                .withModel(calciteModelUri)
                .withSql(
                        "select s.name, count(distinct m.purchase_id) " +
                                "from " +
                                "custom.main m " +
                                "join " +
                                "jdbc.shops s " +
                                "on " +
                                "m.shop_id = cast(s.id as varchar)" +
                                "where " +
                                "s.name like '% Market' " +
                                "group by s.name"
                )
                .expectPilosaExpressions("Count(Row(shop_id=###))", "###", Arrays.asList("1", "6", "7"))
                .run();
    }

    //TODO:
    //next failing test -> implement feature
    //the filters by pilosa table columns will not work now

//    @Test
//    public void testCountJoinOnShopWithFilterByPilosa() {
//        new PilosaAssert()
//                .withModel(calciteModelUri)
//                .withSql(
//                        "select s.name, count(distinct m.purchase_id) " +
//                                "from " +
//                                "custom.main m " +
//                                "join " +
//                                "jdbc.shops s " +
//                                "on " +
//                                "m.shop_id = cast(s.id as varchar)" +
//                                "where " +
//                                "m.shop_id = '1' " +
//                                "group by s.name"
//                )
//                .expectPilosaExpressions("Count(Row(shop_id=\"###\"))", "###", Arrays.asList("20", "21", "22"))
//                .run();
//    }
}
