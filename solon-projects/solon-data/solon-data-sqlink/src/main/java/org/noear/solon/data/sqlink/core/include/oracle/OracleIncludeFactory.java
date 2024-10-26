package org.noear.solon.data.sqlink.core.include.oracle;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlQueryableExpression;
import org.noear.solon.data.sqlink.base.toBean.Include.IncludeBuilder;
import org.noear.solon.data.sqlink.base.toBean.Include.IncludeFactory;
import org.noear.solon.data.sqlink.base.toBean.Include.IncludeSet;
import org.noear.solon.data.sqlink.base.session.SqlSession;

import java.util.Collection;
import java.util.List;

public class OracleIncludeFactory extends IncludeFactory
{
    @Override
    public <T> IncludeBuilder<T> getBuilder(IConfig config, SqlSession session, Class<T> targetClass, Collection<T> sources, List<IncludeSet> includes, ISqlQueryableExpression queryable)
    {
        return new OracleIncludeBuilder<>(config, session, targetClass, sources, includes, queryable);
    }
}
