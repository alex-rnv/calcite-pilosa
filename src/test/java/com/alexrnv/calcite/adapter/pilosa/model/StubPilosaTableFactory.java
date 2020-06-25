package com.alexrnv.calcite.adapter.pilosa.model;

import com.google.common.collect.Lists;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.schema.SchemaPlus;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

/**
 * Hardcoded "Purchases" sample DB;
 */
public class StubPilosaTableFactory extends PilosaTableFactory {

    public static final String TABLE_NAME = "purchases";
    public static final String COLUMN_NAME_PURCHASE = "purchase_id";
    public static final String COLUMN_NAME_TIME = "time";
    public static final String COLUMN_NAME_CUSTOMER = "customer_id";
    public static final String COLUMN_NAME_ITEM = "item_id";
    public static final String COLUMN_NAME_SHOP = "shop_id";
    public static final String COLUMN_NAME_QUANTITY = "quantity";
    public static final String COLUMN_NAME_PRICE_BUCKET = "price_bucket";
    public static final String COLUMN_NAME_IS_ONLINE = "is_online";

    private static final Map<String, Class<?>> COLUMN_NAMES_AND_TYPES = new HashMap<String, Class<?>>() {{
        put(COLUMN_NAME_PURCHASE, Long.class);
        //TODO: time field support requires conceptual architectural decisions
        put(COLUMN_NAME_TIME, Time.class);
        put(COLUMN_NAME_CUSTOMER, Long.class);
        put(COLUMN_NAME_ITEM, Long.class);
        put(COLUMN_NAME_SHOP, Long.class);
        put(COLUMN_NAME_QUANTITY, Long.class);
        put(COLUMN_NAME_PRICE_BUCKET, Long.class);
        //TODO: test boolean support
        put(COLUMN_NAME_IS_ONLINE, Boolean.class);
    }};

    @Override
    public PilosaTable create(SchemaPlus schema, String name, Map<String, Object> operand, RelDataType rowType) {
        return new PilosaTable(StubPilosaAdapter.INSTANCE, TABLE_NAME,
                Lists.newArrayList(COLUMN_NAMES_AND_TYPES.keySet()),
                Lists.newArrayList(COLUMN_NAMES_AND_TYPES.values()));
    }

}
