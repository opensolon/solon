package org.noear.solon.data.sqlink.core.include.oracle;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlQueryableExpression;
import org.noear.solon.data.sqlink.base.toBean.Include.IncludeBuilder;
import org.noear.solon.data.sqlink.base.toBean.Include.IncludeSet;
import org.noear.solon.data.sqlink.base.session.SqlSession;

import java.util.Collection;
import java.util.List;

public class OracleIncludeBuilder<T> extends IncludeBuilder<T>
{
    public OracleIncludeBuilder(IConfig config, SqlSession session, Class<T> targetClass, Collection<T> sources, List<IncludeSet> includes, ISqlQueryableExpression queryable)
    {
        super(config, session, targetClass, sources, includes, queryable);
    }

    @Override
    protected void rowNumber(List<String> rowNumberFunction, List<ISqlExpression> rowNumberParams)
    {
        rowNumberFunction.add("ROW_NUMBER() OVER (PARTITION BY ");
        rowNumberFunction.add(" ORDER BY ");
        if (rowNumberParams.size() <= 1)
        {
            rowNumberParams.add(rowNumberParams.get(0));
        }
        for (int i = 0; i < rowNumberParams.size(); i++)
        {
            if (i < rowNumberParams.size() - 2) rowNumberFunction.add(",");
        }
        rowNumberFunction.add(")");
    }
}
