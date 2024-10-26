package org.noear.solon.data.sqlink.base.toBean.Include;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlQueryableExpression;
import org.noear.solon.data.sqlink.base.session.SqlSession;

import java.util.Collection;
import java.util.List;

public abstract class IncludeFactory
{
    public abstract <T> IncludeBuilder<T> getBuilder(IConfig config, SqlSession session, Class<T> targetClass, Collection<T> sources, List<IncludeSet> includes, ISqlQueryableExpression queryable);
}
