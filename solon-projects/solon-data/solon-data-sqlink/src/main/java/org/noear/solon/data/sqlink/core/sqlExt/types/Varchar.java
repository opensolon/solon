package org.noear.solon.data.sqlink.core.sqlExt.types;

import org.noear.solon.data.sqlink.base.IConfig;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class Varchar extends SqlTypes<String>
{
    private final int length;

    public Varchar(int length)
    {
        this.length = length;
    }

    @Override
    public String getKeyword(IConfig config)
    {
        switch (config.getDbType())
        {
            case MySQL:
                return "CHAR";
            case SQLServer:
            case PostgreSQL:
                return "VARCHAR";
        }
        return String.format("VARCHAR2(%d)", length);
    }
}
