package org.noear.solon.data.sqlink.base;

public interface IDialect
{
    String disambiguation(String property);

    default String disambiguationTableName(String table)
    {
        return disambiguation(table);
    }
}
