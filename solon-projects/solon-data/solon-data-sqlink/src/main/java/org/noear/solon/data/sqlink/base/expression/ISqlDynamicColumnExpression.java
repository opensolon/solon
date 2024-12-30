package org.noear.solon.data.sqlink.base.expression;

import org.noear.solon.data.sqlink.base.SqLinkConfig;

public interface ISqlDynamicColumnExpression extends ISqlExpression {
    void setTableAsName(AsName tableAsName);

    AsName getTableAsName();

    String getColumn();

    @Override
    default ISqlDynamicColumnExpression copy(SqLinkConfig config) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.dynamicColumn(getColumn(), getTableAsName());
    }
}
