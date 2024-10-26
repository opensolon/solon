package org.noear.solon.data.sqlink.core.sqlBuilder;

import org.noear.solon.data.sqlink.base.IConfig;

import java.util.List;

public interface ISqlBuilder
{
    IConfig getConfig();

    String getSql();

    String getSqlAndValue(List<Object> values);
}
