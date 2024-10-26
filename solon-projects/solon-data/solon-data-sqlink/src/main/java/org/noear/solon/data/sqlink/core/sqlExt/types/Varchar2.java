package org.noear.solon.data.sqlink.core.sqlExt.types;

import org.noear.solon.data.sqlink.base.IConfig;

public class Varchar2 extends SqlTypes<String>
{
    private final int length;

    public Varchar2(int length)
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
