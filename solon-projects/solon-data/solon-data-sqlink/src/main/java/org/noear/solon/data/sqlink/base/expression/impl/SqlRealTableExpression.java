package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.expression.ISqlRealTableExpression;

public class SqlRealTableExpression extends SqlTableExpression implements ISqlRealTableExpression
{
    private final Class<?> tableClass;

    SqlRealTableExpression(Class<?> tableClass)
    {
        this.tableClass = tableClass;
    }

    @Override
    public Class<?> getTableClass()
    {
        return tableClass;
    }
}
