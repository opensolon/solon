package org.noear.solon.data.sqlink.core.expression.mysql;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlQueryableExpression;
import org.noear.solon.data.sqlink.base.expression.impl.SqlUnionExpression;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.List;

public class MySqlUnionExpression extends SqlUnionExpression {
    public MySqlUnionExpression(ISqlQueryableExpression queryable, boolean all) {
        super(queryable, all);
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        String union = " UNION" + (all ? " ALL" : "");
        return "(" + queryable.getSqlAndValue(config, values) + ")" + union;
    }
}
