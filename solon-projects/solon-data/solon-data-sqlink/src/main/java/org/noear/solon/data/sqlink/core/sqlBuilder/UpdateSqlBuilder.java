/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.data.sqlink.core.sqlBuilder;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;
import org.noear.solon.data.sqlink.base.IDialect;

import java.util.ArrayList;
import java.util.List;

public class UpdateSqlBuilder implements ISqlBuilder
{
    private final IConfig config;
    private final ISqlJoinsExpression joins;
    private final ISqlSetsExpression sets;
    private final ISqlWhereExpression wheres;
    private final Class<?> target;
    private final SqlExpressionFactory factory;

    public UpdateSqlBuilder(IConfig config, Class<?> target)
    {
        this.config = config;
        this.target = target;
        factory = config.getSqlExpressionFactory();
        joins = factory.Joins();
        sets = factory.sets();
        wheres = factory.where();
    }

    public void addJoin(Class<?> target, JoinType joinType, ISqlTableExpression table, ISqlExpression on)
    {
        ISqlJoinExpression join = factory.join(
                joinType,
                table,
                on,
                1 + joins.getJoins().size()
        );
        joins.addJoin(join);
    }

    public void addSet(ISqlSetsExpression set)
    {
        sets.addSet(set.getSets());
    }

    public void addSet(ISqlSetExpression set)
    {
        sets.addSet(set);
    }

    public void addWhere(ISqlExpression where)
    {
        wheres.addCondition(where);
    }

    public boolean hasWhere()
    {
        return !wheres.isEmpty();
    }

    @Override
    public String getSql()
    {
        return makeUpdate();
    }

    @Override
    public String getSqlAndValue(List<Object> values)
    {
        return makeUpdate();
    }

    public IConfig getConfig()
    {
        return config;
    }

    private String makeUpdate()
    {
        MetaData metaData = MetaDataCache.getMetaData(target);
        IDialect dbConfig = config.getDisambiguation();
        String sql = "UPDATE " + dbConfig.disambiguationTableName(metaData.getTableName()) + " AS t0";
        StringBuilder sb = new StringBuilder();
        sb.append(sql);
        String joinsSqlAndValue = joins.getSql(config);
        if (!joinsSqlAndValue.isEmpty())
        {
            sb.append(" ").append(joinsSqlAndValue);
        }
        String setsSqlAndValue = sets.getSql(config);
        sb.append(" ").append(setsSqlAndValue);
        String wheresSqlAndValue = wheres.getSql(config);
        if (!wheresSqlAndValue.isEmpty())
        {
            sb.append(" ").append(wheresSqlAndValue);
        }
        return sb.toString();
    }

    private String makeUpdate(List<Object> values)
    {
        MetaData metaData = MetaDataCache.getMetaData(target);
        IDialect dbConfig = config.getDisambiguation();
        String sql = "UPDATE " + dbConfig.disambiguationTableName(metaData.getTableName()) + " AS t0";
        List<String> sb = new ArrayList<>();
        sb.add(sql);
        String joinsSqlAndValue = joins.getSqlAndValue(config, values);
        if (!joinsSqlAndValue.isEmpty())
        {
            sb.add(joinsSqlAndValue);
        }
        String setsSqlAndValue = sets.getSqlAndValue(config, values);
        sb.add(setsSqlAndValue);
        String wheresSqlAndValue = wheres.getSqlAndValue(config, values);
        if (!wheresSqlAndValue.isEmpty())
        {
            sb.add(wheresSqlAndValue);
        }
        return String.join(" ", sb);
    }
}
