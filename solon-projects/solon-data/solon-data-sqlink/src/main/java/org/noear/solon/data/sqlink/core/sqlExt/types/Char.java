package org.noear.solon.data.sqlink.core.sqlExt.types;

import org.noear.solon.data.sqlink.base.IConfig;

public class Char extends SqlTypes<Character>
{
    private final int length;

    public Char(int length)
    {
        this.length = length;
    }

    @Override
    public String getKeyword(IConfig config)
    {
        switch (config.getDbType())
        {
            case MySQL:
                return "CHAR(1)";
            case SQLServer:
                return "NCHAR(1)";
        }
        return String.format("CHAR(%d)", length);
    }
}
