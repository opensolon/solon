package org.noear.solon.data.sqlink.core.include.h2;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlQueryableExpression;
import org.noear.solon.data.sqlink.base.toBean.Include.IncludeBuilder;
import org.noear.solon.data.sqlink.base.toBean.Include.IncludeFactory;
import org.noear.solon.data.sqlink.base.toBean.Include.IncludeSet;
import org.noear.solon.data.sqlink.base.session.SqlSession;

import java.util.Collection;
import java.util.List;

public class H2IncludeFactory extends IncludeFactory
{
    @Override
    public <T> IncludeBuilder<T> getBuilder(IConfig config, SqlSession session, Class<T> targetClass, Collection<T> sources, List<IncludeSet> includes, ISqlQueryableExpression queryable)
    {
        return new IncludeBuilder<>(config, session, targetClass, sources, includes, queryable);
    }
}
