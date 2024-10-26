package org.noear.solon.data.sqlink.base.expression.impl;


import org.noear.solon.data.sqlink.base.expression.ISqlTableExpression;

public abstract class SqlTableExpression implements ISqlTableExpression
{
    public abstract Class<?> getTableClass();
}
