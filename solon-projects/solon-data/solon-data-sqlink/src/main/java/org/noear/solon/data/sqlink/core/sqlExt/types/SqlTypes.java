package org.noear.solon.data.sqlink.core.sqlExt.types;

import org.noear.solon.data.sqlink.base.sqlExt.ISqlKeywords;

/**
 * @author kiryu1223
 * @since 3.0
 */
public abstract class SqlTypes<T> implements ISqlKeywords
{
    public static Varchar varchar2()
    {
        return new Varchar(255);
    }

    public static Varchar varchar2(int length)
    {
        return new Varchar(length);
    }

    public static Char Char()
    {
        return new Char(4);
    }

    public static Char Char(int length)
    {
        return new Char(length);
    }
}
