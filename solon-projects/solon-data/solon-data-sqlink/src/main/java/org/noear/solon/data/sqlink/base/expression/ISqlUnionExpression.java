package org.noear.solon.data.sqlink.base.expression;

import org.noear.solon.data.sqlink.base.SqLinkConfig;

public interface ISqlUnionExpression extends ISqlExpression {
    ISqlQueryableExpression getQueryable();
    boolean isAll();

    @Override
    default ISqlUnionExpression copy(SqLinkConfig config) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.union(getQueryable().copy(config),isAll());
    }
}
