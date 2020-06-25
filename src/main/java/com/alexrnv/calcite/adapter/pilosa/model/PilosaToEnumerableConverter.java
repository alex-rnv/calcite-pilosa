package com.alexrnv.calcite.adapter.pilosa.model;

import com.alexrnv.calcite.adapter.pilosa.expression.PilosaQueryBuilder;
import org.apache.calcite.adapter.enumerable.*;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.linq4j.tree.*;
import org.apache.calcite.plan.*;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterImpl;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.util.BuiltInMethod;
import org.apache.calcite.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class PilosaToEnumerableConverter extends ConverterImpl implements EnumerableRel {

    public PilosaToEnumerableConverter(RelOptCluster cluster, RelTraitSet traits, RelNode child) {
        super(cluster, ConventionTraitDef.INSTANCE, traits, child);
    }

    @Override
    public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
        return new PilosaToEnumerableConverter(getCluster(), traitSet, sole(inputs));
    }

    @Override
    public Result implement(EnumerableRelImplementor implementor, Prefer pref) {
        final PilosaQueryBuilder pilosaQueryBuilder = new PilosaQueryBuilder();
        final PilosaRelTreeVisitor pilosaRelTreeVisitor = new PilosaRelTreeVisitor(pilosaQueryBuilder);
        pilosaRelTreeVisitor.visitChild(getInput());
        String queryString = pilosaQueryBuilder.build();

        final PhysType physType = getPhysType(implementor.getTypeFactory(), pref);
        final BlockStatement blockStatement = prepareBlockStatement(implementor, pilosaRelTreeVisitor, queryString, physType);

        return implementor.result(physType, blockStatement);
    }

    private PhysType getPhysType(JavaTypeFactory typeFactory, Prefer pref) {
        return PhysTypeImpl.of(typeFactory, rowType, pref.prefer(JavaRowFormat.ARRAY));
    }

    private BlockStatement prepareBlockStatement(EnumerableRelImplementor implementor, PilosaRelTreeVisitor pilosaRelTreeVisitor, String queryString, PhysType physType) {
        BlockBuilder blockBuilder = new BlockBuilder();
        Expression fields = createFieldsExpression(physType, blockBuilder);
        Expression table = createTableExpression(pilosaRelTreeVisitor.getRelOptTable(), blockBuilder);
        Expression query = createQueryExpression(implementor, pilosaRelTreeVisitor.getCorrelationFieldsHolder(), queryString, blockBuilder);
        blockBuilder.add(Expressions.call(table, "queryPilosa", query,  fields));
        return blockBuilder.toBlock();
    }

    private Expression createFieldsExpression(PhysType physType, BlockBuilder blockBuilder) {
        final RelDataType rowType = getRowType();
        return blockBuilder.append("fields",
                constantArrayList(
                        Pair.zip(rowType.getFieldNames(),
                                new AbstractList<Class>() {
                                    @Override public Class get(int index) {
                                        return physType.fieldClass(index);
                                    }

                                    @Override public int size() {
                                        return rowType.getFieldCount();
                                    }
                                }),
                        Pair.class));
    }

    private Expression createTableExpression(RelOptTable relOptTable, BlockBuilder blockBuilder) {
        return blockBuilder.append("table", relOptTable.getExpression(PilosaTable.PilosaQueryable.class));
    }

    private Expression createQueryExpression(EnumerableRelImplementor implementor, CorrelationFieldsHolder correlationFieldsHolder, String queryString, BlockBuilder blockBuilder) {
        return new PilosaToLinq4jExpressionConverter(implementor, correlationFieldsHolder, queryString).appendToBlock(blockBuilder);
    }

    /** E.g. {@code constantArrayList("x", "y")} returns
     * "Arrays.asList('x', 'y')". */
    private static <T> MethodCallExpression constantArrayList(List<T> values,
                                                              Class clazz) {
        return Expressions.call(
                BuiltInMethod.ARRAYS_AS_LIST.method,
                Expressions.newArrayInit(clazz, constantList(values)));
    }

    /** E.g. {@code constantList("x", "y")} returns
     * {@code {ConstantExpression("x"), ConstantExpression("y")}}. */
    private static <T> List<Expression> constantList(List<T> values) {
        return values.stream().map(Expressions::constant).collect(Collectors.toList());
    }

}
