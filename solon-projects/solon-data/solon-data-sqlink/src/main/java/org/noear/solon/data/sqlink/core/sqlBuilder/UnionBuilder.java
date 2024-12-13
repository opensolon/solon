package org.noear.solon.data.sqlink.core.sqlBuilder;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.ArrayList;
import java.util.List;

public class UnionBuilder implements ISqlBuilder {
    private final SqLinkConfig config;
    private final ISqlQueryableExpression queryable;
    private final ISqlUnionsExpression unions;
    private final ISqlOrderByExpression orderBy;
    private final ISqlLimitExpression limit;

    public UnionBuilder(SqLinkConfig config, ISqlQueryableExpression queryable, ISqlUnionsExpression unions, ISqlOrderByExpression orderBy, ISqlLimitExpression limit) {
        this.config = config;
        this.queryable = queryable;
        this.unions = unions;
        this.orderBy = orderBy;
        this.limit = limit;
    }

    public void addUnion(ISqlUnionExpression unionExpression) {
        unions.addUnion(unionExpression);
    }

    public void addOrder(ISqlOrderExpression orderExpression) {
        orderBy.addOrder(orderExpression);
    }

    public void addLimit(long offset, long rows) {
        limit.setOffset(offset);
        limit.setRows(rows);
    }

    public boolean isSingle() {
        return queryable.getSelect().isSingle();
    }

    public List<FieldMetaData> getMappingData() {
        return queryable.getMappingData();
    }

    public <T> Class<T> getTargetClass() {
        return (Class<T>) queryable.getMainTableClass();
    }

    @Override
    public SqLinkConfig getConfig() {
        return config;
    }

    @Override
    public String getSql() {
        List<String> strings = new ArrayList<>(4);
        strings.add("(" + queryable.getSql(config) + ")");
        strings.add(unions.getSql(config));
        String orderBySql = orderBy.getSql(config);
        if (!orderBySql.isEmpty()) {
            strings.add(orderBySql);
        }
        String limitSql = limit.getSql(config);
        if (!limitSql.isEmpty()) {
            strings.add(limitSql);
        }
        return String.join(" ", strings);
    }

    @Override
    public String getSqlAndValue(List<SqlValue> values) {
        List<String> strings = new ArrayList<>(4);
        strings.add("(" + queryable.getSqlAndValue(config, values) + ")");
        strings.add(unions.getSqlAndValue(config, values));
        String orderBySql = orderBy.getSqlAndValue(config, values);
        if (!orderBySql.isEmpty()) {
            strings.add(orderBySql);
        }
        String limitSql = limit.getSqlAndValue(config, values);
        if (!limitSql.isEmpty()) {
            strings.add(limitSql);
        }
        return String.join(" ", strings);
    }
}
