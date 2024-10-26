package org.noear.solon.data.sqlink.base.expression;

import org.noear.solon.data.sqlink.base.IConfig;

import java.util.List;

public interface ISqlExpression
{
    String getSqlAndValue(IConfig config, List<Object> values);

    default String getSql(IConfig config)
    {
        return getSqlAndValue(config, null);
    }

    <T extends ISqlExpression> T copy(IConfig config);
}
