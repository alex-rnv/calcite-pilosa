package com.alexrnv.calcite.adapter.pilosa.model;

import org.junit.Test;

import java.sql.SQLException;


public class SqlPilosaQueryTest {

    @Test(expected = SQLException.class)
    public void testCountAllWithoutFiltersIsNotSupported() {
        new PilosaAssert()
                .withSql("select count(*) from custom.main")
                .expectPilosaExpression("Count(Row())")
                .run();
    }

    @Test
    public void testOneFilter() {
        new PilosaAssert()
                .withSql(
                        "select count(*) " +
                                "from custom.main " +
                                "where " +
                                "quantity=2147427245"
                )
                .expectPilosaExpression("Count(Row(quantity=2147427245))")
                .run();
    }

    @Test
    public void testAnd() {
        new PilosaAssert()
                .withSql(
                        "select count(*) " +
                                "from custom.main " +
                                "where " +
                                "quantity='2147427245' " +
                                "and shop_id = '1'"
                )
                .expectPilosaExpression("Count(Intersect(Row(quantity=2147427245),Row(shop_id=1)))")
                .run();
    }

    @Test
    public void testOr() {
        new PilosaAssert()
                .withSql(
                        "select count(*) " +
                                "from custom.main " +
                                "where " +
                                "quantity='2147427245' " +
                                "or quantity = '0'"
                )
                .expectPilosaExpression("Count(Union(Row(quantity=2147427245),Row(quantity=0)))")
                .run();
    }

    @Test
    public void testIn() {
        new PilosaAssert()
                .withSql(
                        "select count(*) " +
                                "from custom.main " +
                                "where " +
                                "quantity in ('2147427245', '199576232')"
                )
                .expectPilosaExpression("Count(Union(Row(quantity=2147427245),Row(quantity=199576232)))")
                .run();
    }

    @Test
    public void testAndOrAnd() {
        new PilosaAssert()
                .withSql(
                        "select count(*) " +
                                "from custom.main " +
                                "where " +
                                "quantity='2147427245' " +
                                "and shop_id = '1'" +
                                "or " +
                                "price_bucket = '2' " +
                                "and item_id = '1'"
                )
                .expectPilosaExpression("Count(Union(Intersect(Row(quantity=2147427245),Row(shop_id=1)),Intersect(Row(price_bucket=2),Row(item_id=1))))")
                .run();
    }

    @Test
    public void testAndOrOr() {
        new PilosaAssert()
                .withSql(
                        "select count(*) " +
                                "from custom.main " +
                                "where " +
                                "quantity='2147427245' " +
                                "and shop_id = '1'" +
                                "or " +
                                "price_bucket = '2'" +
                                "or " +
                                "item_id = '2'"
                )
                .expectPilosaExpression("Count(Union(Intersect(Row(quantity=2147427245),Row(shop_id=1)),Row(price_bucket=2),Row(item_id=2)))")
                .run();
    }

    @Test
    public void testInAndIn() {
        new PilosaAssert()
                .withSql(
                        "select count(*) " +
                                "from custom.main " +
                                "where " +
                                "quantity in ('2147427245', '199576232') " +
                                "and " +
                                "shop_id in ('1', '2')"
                )
                .expectPilosaExpression("Count(Intersect(Union(Row(quantity=2147427245),Row(quantity=199576232)),Union(Row(shop_id=1),Row(shop_id=2))))")
                .run();
    }

    @Test
    public void testInOrIn() {
        new PilosaAssert()
                .withSql(
                        "select count(*) " +
                                "from custom.main " +
                                "where " +
                                "quantity in (2147427245, 199576232) " +
                                "or " +
                                "item_id in ('2', '3')"
                )
                .expectPilosaExpression("Count(Union(Row(quantity=2147427245),Row(quantity=199576232),Row(item_id=2),Row(item_id=3)))")
                .run();
    }

    @Test
    public void testAndAndOrOrAnd() {
        new PilosaAssert()
                .withSql(
                        "select count(*) " +
                                "from custom.main " +
                                "where " +
                                "quantity='2147427245' " +
                                "and shop_id = '1' " +
                                "and is_online = '0'" +
                                "or " +
                                "is_online = '1'" +
                                "or " +
                                "item_id = '2' " +
                                "and price_bucket='12'"
                )
                .expectPilosaExpression("Count(Union(Intersect(Row(quantity=2147427245),Row(shop_id=1),Row(is_online=0)),Row(is_online=1),Intersect(Row(item_id=2),Row(price_bucket=12))))")
                .run();
    }

    @Test
    public void testIntersect() {
        new PilosaAssert()
                .withSql(
                        "select count(distinct purchase_id) as volume " +
                                "from " +
                                "(" +
                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "item_id = '2' " +

                                "intersect " +

                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "item_id = '4'" +
                                ")"
                )
                .expectPilosaExpression("Count(Intersect(Row(item_id=2),Row(item_id=4)))")
                .run();
    }

    @Test
    public void testIntersectColumnWithColumnsUnion() {
        new PilosaAssert()
                .withSql(
                        "select count(distinct purchase_id) as volume " +
                                "from " +
                                "(" +
                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity = '2147422742' " +
                                "or " +
                                "quantity = '2147422744'" +

                                "intersect " +

                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity = '2147422743'" +
                                ")"
                )
                .expectPilosaExpression("Count(Intersect(Union(Row(quantity=2147422742),Row(quantity=2147422744)),Row(quantity=2147422743)))")
                .run();
    }

    @Test
    public void testIntersectColumnUnionWithColumnsOverlap() {
        new PilosaAssert()
                .withSql(
                        "select count(distinct purchase_id) as volume " +
                                "from " +
                                "(" +
                                    "select purchase_id " +
                                    "from custom.main " +
                                    "where " +
                                    "quantity = '2147422742' " +
                                    "or " +
                                    "quantity = '2147422744'" +

                                "intersect " +

                                    "select purchase_id " +
                                    "from custom.main " +
                                    "where " +
                                    "quantity = '2147422743' " +
                                    "and item_id = '1'" +
                                ")"
                )
                .expectPilosaExpression("Count(Intersect(Union(Row(quantity=2147422742),Row(quantity=2147422744)),Intersect(Row(quantity=2147422743),Row(item_id=1))))")
                .run();
    }

    @Test
    public void testIntersectWithIn() {
        new PilosaAssert()
                .withSql(
                        "select count(distinct purchase_id) as volume " +
                                "from " +
                                "(" +
                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity in ('2147422742', '2147422744') " +

                            "intersect " +

                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity in ('2147422743', '2147422745') " +
                                ")"
                )
                .expectPilosaExpression("Count(Intersect(Union(Row(quantity=2147422742),Row(quantity=2147422744)),Union(Row(quantity=2147422743),Row(quantity=2147422745))))")
                .run();
    }

    @Test
    public void testMultipleIntersectsWithIn() {
        new PilosaAssert()
                .withSql(
                        "select count(distinct purchase_id) as volume " +
                                "from " +
                                "(" +
                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity in (0, 1) " +

                                "intersect " +

                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity in (2, 3) " +

                                "intersect " +

                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity in (4, 5) " +
                                ")"
                )
                .expectPilosaExpression("Count(Intersect(Union(Row(quantity=0),Row(quantity=1)),Union(Row(quantity=2),Row(quantity=3)),Union(Row(quantity=4),Row(quantity=5))))")
                .run();
    }

    @Test
    public void testMultipleIntersectsWithAndOr() {
        new PilosaAssert()
                .withSql(
                        "select count(distinct purchase_id) as volume " +
                                "from " +
                                "(" +
                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity = 0 and item_id = 'XX' " +

                                "intersect " +

                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity = 2 or quantity = 3 " +

                                "intersect " +

                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity = 4 and shop_id = '3' " +
                                ")"
                )
                .expectPilosaExpression("Count(Intersect(Intersect(Row(quantity=0),Row(item_id=XX)),Union(Row(quantity=2),Row(quantity=3)),Intersect(Row(quantity=4),Row(shop_id=3))))")
                .run();
    }

    @Test
    public void test4Intersects() {
        new PilosaAssert()
                .withSql(
                        "select count(distinct purchase_id) as volume " +
                                "from " +
                                "(" +
                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity = '0' " +

                                "intersect " +

                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity = '1' " +

                                "intersect " +

                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity = '2' " +

                                "intersect " +

                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity = '3'" +
                                ")"
                )
                .expectPilosaExpression("Count(Intersect(Row(quantity=0),Row(quantity=1),Row(quantity=2),Row(quantity=3)))")
                .run();
    }

    @Test
    public void testGroupBy() {
        new PilosaAssert()
                .withSql(
                        "select item_id, count(distinct purchase_id) as volume " +
                                "from custom.main " +
                                "group by item_id"
                )
                .expectPilosaExpression("GroupBy(Rows(item_id))")
                .run();
    }

    @Test
    public void testGroupByColumnFilterByAnotherColumn() {
        new PilosaAssert()
                .withSql(
                        "select shop_id, count(distinct purchase_id) as volume " +
                                "from custom.main " +
                                "where " +
                                    "item_id = '2' " +
                                "group by shop_id"
                )
                .expectPilosaExpression("GroupBy(Rows(shop_id),filter=Row(item_id=2))")
                .run();
    }

//    @Test(expected = java.sql.SQLException.class)
//    public void testGroupByPublisherAndLanguageFilterByContributorsUnion() {
//        new PilosaAssert()
//                .withSql(
//                        "select shop_id, item_id, count(distinct purchase_id) as volume " +
//                                "from custom.main " +
//                                "where " +
//                                    "quantity = '2147424192' " +
//                                "or " +
//                                    "quantity = '2147430396' " +
//                                "group by shop_id, item_id"
//                )
//                .expectPilosaExpression("GroupBy(Rows(item_id),Rows(shop_id),filter=Union(Row(quantity=2147424192),Row(quantity=2147430396)))")
//                .run();
//    }

//    @Test(expected = java.sql.SQLException.class)
//    public void testGroupByOsTypeAndLanguageFilterByContributorsUnion() {
//        new PilosaAssert()
//                .withSql(
//                        "select price_bucket, item_id, count(distinct purchase_id) as volume " +
//                                "from custom.main " +
//                                "where " +
//                                "quantity = '2147424192' " +
//                                "or " +
//                                "quantity = '2147430396' " +
//                                "group by price_bucket, item_id"
//                )
//                .expectPilosaExpression("GroupBy(Rows(item_id),Rows(price_bucket),filter=Union(Row(quantity=2147424192),Row(quantity=2147430396)))")
//                .run();
//    }

    //todo
//    @Test(expected = java.sql.SQLException.class)
//    public void testGroupByPublisherPlatformAndLanguageFilterByBrowser() {
//        new PilosaAssert()
//                .withSql(
//                        "select shop_id, customer_id, item_id, count(distinct purchase_id) as volume " +
//                                "from custom.main " +
//                                "where " +
//                                "ua_browser_name = '12' " +
//                                "group by shop_id, customer_id, item_id"
//                )
//                .expectPilosaExpression("GroupBy(Rows(customer_id),Rows(item_id),Rows(shop_id),filter=Row(ua_browser_name=12))")
//                .run();
//    }

//    @Test(expected = java.sql.SQLException.class)
//    public void testGroupByPublisherPlatformAndOsTypeFilterByNotRobot() {
//        new PilosaAssert()
//                .withSql(
//                        "select shop_id, customer_id, price_bucket, count(distinct purchase_id) as volume " +
//                                "from custom.main " +
//                                "where " +
//                                "item_id = '2' " +
//                                "group by shop_id, customer_id, price_bucket"
//                )
//                .expectPilosaExpression("GroupBy(Rows(customer_id),Rows(shop_id),Rows(price_bucket),filter=Row(item_id=false))")
//                .run();
//    }

    @Test
    public void testGroupByWithInFilter() {
        new PilosaAssert()
                .withSql(
                        "select item_id, count(distinct purchase_id) as volume " +
                                "from custom.main " +
                                "where " +
                                "price_bucket in ('0', '1', '12') " +
                                "group by item_id"
                )
                .expectPilosaExpression("GroupBy(Rows(item_id),filter=Union(Row(price_bucket=0),Row(price_bucket=1),Row(price_bucket=12)))")
                .run();
    }

    //TODO
//    @Test(expected = SQLException.class)
//    public void testGroupByContributorsThrowsException() {
//        new PilosaAssert()
//                .withSql(
//                        "select shop_id, quantity, count(distinct purchase_id) as volume " +
//                                "from custom.main"
//                )
//                .expectPilosaExpression("GroupBy(Rows(shop_id),Rows(item_id),filter=Union(Row(quantity=2147424192),Row(quantity=2147430396)))")
//                .run();
//    }

    @Test
    public void testExcept() {
        new PilosaAssert()
                .withSql(
                        "select count(distinct purchase_id) as volume " +
                                "from " +
                                "(" +
                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "item_id = '2' " +

                                "except " +

                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "item_id = '4'" +
                                ")"
                )
                .expectPilosaExpression("Count(Difference(Row(item_id=2),Row(item_id=4)))")
                .run();
    }

    @Test
    public void testExceptWithIn() {
        new PilosaAssert()
                .withSql(
                        "select count(distinct purchase_id) as volume " +
                                "from " +
                                "(" +
                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "item_id in ('2', '4', '3') " +

                                "except " +

                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "item_id = '5'" +
                                ")"
                )
                .expectPilosaExpression("Count(Difference(Union(Row(item_id=2),Row(item_id=4),Row(item_id=3)),Row(item_id=5)))")
                .run();
    }

    @Test
    public void testColumnUnionExceptAnotherColumn() {
        new PilosaAssert()
                .withSql(
                        "select count(distinct purchase_id) as volume " +
                                "from " +
                                "(" +
                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity ='0' or quantity = '1' " +

                                "except " +

                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "shop_id = '3'" +
                                ")"
                )
                .expectPilosaExpression("Count(Difference(Union(Row(quantity=0),Row(quantity=1)),Row(shop_id=3)))")
                .run();
    }

    @Test
    public void testUnionExceptUnion() {
        new PilosaAssert()
                .withSql(
                        "select count(distinct purchase_id) as volume " +
                                "from " +
                                "(" +
                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity ='0' or quantity = '1' " +

                                "except " +

                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity in ('2','3') " +
                                ")"
                )
                .expectPilosaExpression("Count(Difference(Union(Row(quantity=0),Row(quantity=1)),Union(Row(quantity=2),Row(quantity=3))))")
                .run();
    }

    @Test
    public void testMultipleExcept() {
        new PilosaAssert()
                .withSql(
                        "select count(distinct purchase_id) as volume " +
                                "from " +
                                "(" +
                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity = '0' " +

                                "except " +

                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "shop_id in ('3', '4') " +

                                "except " +

                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "item_id = '6' " +
                                ")"
                )
                .expectPilosaExpression("Count(Difference(Row(quantity=0),Union(Row(shop_id=3),Row(shop_id=4)),Row(item_id=6)))")
                .run();
    }

    @Test
    public void test4Excepts() {
        new PilosaAssert()
                .withSql(
                        "select count(distinct purchase_id) as volume " +
                                "from " +
                                "(" +
                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity ='0' " +

                                "except " +

                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity = '1' " +

                                "except " +

                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity = '2' " +

                                "except " +

                                "select purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity = '3' " +
                                ")"
                )
                .expectPilosaExpression("Count(Difference(Row(quantity=0),Row(quantity=1),Row(quantity=2),Row(quantity=3)))")
                .run();
    }

    @Test
    public void testIntersectColumnGroupByAnotherColumn() {
        new PilosaAssert()
                .withSql(
                        "select item_id, count(distinct purchase_id) as volume " +
                                "from " +
                                "(" +
                                "select item_id, purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity = '1' " +

                                "intersect " +

                                "select item_id, purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity = '2'" +
                                ") " +
                                "group by item_id"
                )
                .expectPilosaExpression("GroupBy(Rows(item_id),filter=Intersect(Row(quantity=1),Row(quantity=2)))")
                .run();
    }

    @Test
    public void testExceptColumnGroupByAnotherColumn() {
        new PilosaAssert()
                .withSql(
                        "select shop_id, count(distinct purchase_id) as volume " +
                                "from " +
                                "(" +
                                "select shop_id, purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity in ('1', '2') " +

                                "except " +

                                "select shop_id, purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity = '7'" +
                                ") " +
                                "group by shop_id"
                )
                .expectPilosaExpression("GroupBy(Rows(shop_id),filter=Difference(Union(Row(quantity=1),Row(quantity=2)),Row(quantity=7)))")
                .run();
    }

    @Test
    public void testMultipleIntersectsGroupByColumn() {
        new PilosaAssert()
                .withSql(
                        "select customer_id, count(distinct purchase_id) as volume " +
                                "from " +
                                "(" +
                                "select customer_id, purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity = '1' " +

                                "intersect " +

                                "select customer_id, purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity = '2'" +

                                "intersect " +

                                "select customer_id, purchase_id " +
                                "from custom.main " +
                                "where " +
                                "quantity = '3'" +
                                ") " +
                                "group by customer_id"
                )
                .expectPilosaExpression("GroupBy(Rows(customer_id),filter=Intersect(Row(quantity=1),Row(quantity=2),Row(quantity=3)))")
                .run();
    }

}
