package org.noear.solon.data.sqlink.base.expression;

import org.noear.solon.data.sqlink.base.SqLinkConfig;

public interface ISqlRecursionExpression extends ISqlWithExpression {
    ISqlQueryableExpression getQueryable();

    String recursionKeyword();

    String withTableName();

    String parentId();

    String childId();

    int level();

    @Override
    default ISqlRecursionExpression copy(SqLinkConfig config) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.recursion(getQueryable(),parentId(),childId(),level());
    }
}
