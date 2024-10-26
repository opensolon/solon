package org.noear.solon.data.sqlink.base.expression;

public enum JoinType
{
    INNER,
    LEFT,
    RIGHT,
    ;

    public String getJoin()
    {
        return name() + " JOIN";
    }
}
