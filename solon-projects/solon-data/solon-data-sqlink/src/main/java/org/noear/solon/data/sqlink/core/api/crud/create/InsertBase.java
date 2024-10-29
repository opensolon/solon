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
package org.noear.solon.data.sqlink.core.api.crud.create;

import org.noear.solon.data.sqlink.core.api.crud.CRUD;
import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.metaData.IConverter;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;
import org.noear.solon.data.sqlink.base.metaData.PropertyMetaData;
import org.noear.solon.data.sqlink.base.session.SqlValue;
import org.noear.solon.data.sqlink.core.sqlBuilder.InsertSqlBuilder;
import org.noear.solon.data.sqlink.base.IDialect;
import org.noear.solon.data.sqlink.base.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.cast;

/**
 * @author kiryu1223
 * @since 3.0
 */
public abstract class InsertBase extends CRUD
{
    public final static Logger log = LoggerFactory.getLogger(InsertBase.class);

    private final InsertSqlBuilder sqlBuilder;

    protected InsertSqlBuilder getSqlBuilder()
    {
        return sqlBuilder;
    }

    protected IConfig getConfig()
    {
        return sqlBuilder.getConfig();
    }

    public InsertBase(IConfig c)
    {
        this.sqlBuilder = new InsertSqlBuilder(c);
    }

    /**
     * 执行sql语句
     * @return 执行后的结果
     */
    public long executeRows()
    {
        List<Object> objects = getObjects();
        if (!objects.isEmpty())
        {
            return objectsExecuteRows(objects);
        }
        else
        {
            return 0;
        }
    }

    public String toSql()
    {
        List<Object> objects = getObjects();
        if (!objects.isEmpty())
        {
            return makeByObjects(objects, null);
        }
        else
        {
            return sqlBuilder.getSql();
        }
    }

    protected <T> List<T> getObjects()
    {
        return Collections.emptyList();
    }

    protected abstract <T> Class<T> getTableType();

    private long objectsExecuteRows(List<Object> objects)
    {
        IConfig config = getConfig();
        List<SqlValue> sqlValues = new ArrayList<>();
        String sql = makeByObjects(objects, sqlValues);
        tryPrintUseDs(log,config.getDataSourceManager().getDsKey());
        tryPrintSql(log, sql);
        SqlSession session = config.getSqlSessionFactory().getSession();
        if (objects.size() > 1)
        {
            tryPrintBatch(log,objects.size());
            return session.batchExecuteUpdate(sql, objects.size(), sqlValues);
        }
        else
        {
            tryPrintNoBatch(log,objects.size());
            return session.executeUpdate(sql, sqlValues);
        }
    }

    private String makeByObjects(List<Object> objects, List<SqlValue> sqlValues)
    {
        MetaData metaData = MetaDataCache.getMetaData(getTableType());
        List<String> tableFields = new ArrayList<>();
        List<String> tableValues = new ArrayList<>();
        for (PropertyMetaData pro : metaData.getNotIgnorePropertys())
        {
            IConverter<?, ?> converter = pro.getConverter();
            tableFields.add(pro.getColumn());
            tableValues.add("?");
            if (sqlValues == null) continue;
            List<Object> values = new ArrayList<>(objects.size());
            if (pro.hasConverter())
            {
                for (Object o : objects)
                {
                    try
                    {
                        Object obj = pro.getGetter().invoke(o);
                        if (obj != null)
                        {
                            values.add(converter.toDb(cast(obj), pro));
                        }
                        else
                        {
                            values.add(null);
                        }
                    }
                    catch (IllegalAccessException | InvocationTargetException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
                sqlValues.add(new SqlValue(pro.getDbType(), values));
            }
            else
            {
                for (Object o : objects)
                {
                    try
                    {
                        values.add(pro.getGetter().invoke(o));
                    }
                    catch (IllegalAccessException | InvocationTargetException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
                sqlValues.add(new SqlValue(pro.getType(), values));
            }

        }
        IDialect dialect = getSqlBuilder().getConfig().getDisambiguation();
        return "INSERT INTO " + dialect.disambiguationTableName(metaData.getTableName()) + "(" + String.join(",", tableFields)
                + ") VALUES(" + String.join(",", tableValues) + ")";
    }
}
