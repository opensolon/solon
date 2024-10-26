package org.noear.solon.data.sqlink.base.expression;


import org.noear.solon.data.sqlink.base.IConfig;

import java.util.Collection;
import java.util.List;

public interface ISqlSetsExpression extends ISqlExpression
{
    List<ISqlSetExpression> getSets();

    void addSet(ISqlSetExpression sqlSetExpression);

    void addSet(Collection<ISqlSetExpression> set);

    @Override
    default ISqlSetsExpression copy(IConfig config)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        ISqlSetsExpression newSets = factory.sets();
        for (ISqlSetExpression set : getSets())
        {
            newSets.addSet(set.copy(config));
        }
        return newSets;
    }
}
