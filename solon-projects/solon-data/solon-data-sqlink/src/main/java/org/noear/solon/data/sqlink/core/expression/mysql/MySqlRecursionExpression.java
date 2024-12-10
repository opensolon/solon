package org.noear.solon.data.sqlink.core.expression.mysql;

import org.noear.solon.data.sqlink.base.expression.ISqlQueryableExpression;
import org.noear.solon.data.sqlink.base.expression.impl.SqlRecursionExpression;

public class MySqlRecursionExpression extends SqlRecursionExpression {
    public MySqlRecursionExpression(ISqlQueryableExpression queryable, String parentId, String childId, int level) {
        super(queryable, parentId, childId, level);
    }

    @Override
    public String recursionKeyword() {
        return "RECURSIVE";
    }
}
