package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlQueryableExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlUnionExpression;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.List;

public class SqlUnionExpression implements ISqlUnionExpression {
    protected final ISqlQueryableExpression queryable;
    protected final boolean all;

    public SqlUnionExpression(ISqlQueryableExpression queryable, boolean all) {
        this.queryable = queryable;
        this.all = all;
    }

    @Override
    public ISqlQueryableExpression getQueryable() {
        return queryable;
    }

    @Override
    public boolean isAll() {
        return all;
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        String union = "UNION" + (all ? " ALL " : " ");
        return union + "(" + queryable.getSqlAndValue(config, values) + ")";
    }
}
