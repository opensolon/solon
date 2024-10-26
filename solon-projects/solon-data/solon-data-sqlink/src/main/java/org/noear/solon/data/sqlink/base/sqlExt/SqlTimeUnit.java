package org.noear.solon.data.sqlink.base.sqlExt;

import org.noear.solon.data.sqlink.base.IConfig;

public enum SqlTimeUnit implements ISqlKeywords
{
    YEAR,
    MONTH,
    WEEK,
    DAY,
    HOUR,
    MINUTE,
    SECOND,
    MILLISECOND,
    MICROSECOND,
    NANOSECOND,
    ;

    @Override
    public String getKeyword(IConfig config)
    {
        return name();
    }
}
