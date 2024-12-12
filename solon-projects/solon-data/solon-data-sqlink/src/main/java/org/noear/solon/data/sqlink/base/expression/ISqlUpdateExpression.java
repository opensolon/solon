package org.noear.solon.data.sqlink.base.expression;

import org.noear.solon.data.sqlink.base.SqLinkConfig;

public interface ISqlUpdateExpression extends ISqlExpression {
    ISqlFromExpression getFrom();

    ISqlJoinsExpression getJoins();

    ISqlSetsExpression getSets();

    ISqlWhereExpression getWhere();

    void addJoin(ISqlJoinExpression join);

    void addSet(ISqlSetExpression set);

    void addWhere(ISqlExpression where);

    @Override
    default ISqlUpdateExpression copy(SqLinkConfig config) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.update(getFrom().copy(config), getJoins().copy(config), getSets().copy(config), getWhere().copy(config));
    }
}
