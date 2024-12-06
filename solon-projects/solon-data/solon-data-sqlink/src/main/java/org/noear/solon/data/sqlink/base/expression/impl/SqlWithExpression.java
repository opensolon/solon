package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlQueryableExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlWithExpression;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.List;

public class SqlWithExpression implements ISqlWithExpression {

    protected final ISqlQueryableExpression queryable;
    protected final String name;

    public SqlWithExpression(ISqlQueryableExpression queryable, String name) {
        this.queryable = queryable;
        this.name = name;
    }

    @Override
    public ISqlQueryableExpression getQueryable() {
        return queryable;
    }

    @Override
    public String withTableName() {
        return name;
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        return name + " AS (" + queryable.getSqlAndValue(config, values) + ")";
    }
}
