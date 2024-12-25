package org.noear.solon.data.sqlink.base.expression;

public interface ISqlDynamicColumnExpression extends ISqlExpression {
    void setTableAsName(String tableAsName);

    String getTableAsName();

    String getColumn();
}
