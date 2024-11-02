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
package org.noear.solon.data.sqlink.core;

import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;
import org.noear.solon.data.sqlink.base.toBean.handler.TypeHandlerManager;
import org.noear.solon.data.sqlink.core.api.client.Client;
import org.noear.solon.data.sqlink.base.DbType;
import org.noear.solon.data.sqlink.base.transaction.DefaultTransactionManager;
import org.noear.solon.data.sqlink.base.transaction.TransactionManager;
import org.noear.solon.data.sqlink.base.toBean.beancreator.BeanCreatorFactory;
import org.noear.solon.data.sqlink.base.dataSource.DataSourceManager;
import org.noear.solon.data.sqlink.base.session.DefaultSqlSessionFactory;
import org.noear.solon.data.sqlink.base.session.SqlSessionFactory;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class SQLink
{
    private SQLink()
    {
    }

    private DbType dbType = DbType.MySQL;
    private Option option = new Option();
    private DataSourceManager dataSourceManager;
    private TransactionManager transactionManager;
    private SqlSessionFactory sqlSessionFactory;
    private BeanCreatorFactory beanCreatorFactory;

    public Client build()
    {
        if (dataSourceManager == null)
        {
            throw new NullPointerException("dataSourceManager is null");
        }
        if (transactionManager == null)
        {
            transactionManager = new DefaultTransactionManager(dataSourceManager);
        }
        if (sqlSessionFactory == null)
        {
            sqlSessionFactory = new DefaultSqlSessionFactory(dataSourceManager, transactionManager);
        }
        if (beanCreatorFactory == null)
        {
            beanCreatorFactory = new BeanCreatorFactory();
        }
        Config config = new Config(option, dbType, transactionManager, dataSourceManager, sqlSessionFactory, beanCreatorFactory);
        return new Client(config);
    }

    public static SQLink bootStrap()
    {
        return new SQLink();
    }

    public SQLink setDataSourceManager(DataSourceManager dataSourceManager)
    {
        this.dataSourceManager = dataSourceManager;
        return this;
    }

    public SQLink setTransactionManager(TransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
        return this;
    }

    public SQLink setSqlSessionFactory(SqlSessionFactory sqlSessionFactory)
    {
        this.sqlSessionFactory = sqlSessionFactory;
        return this;
    }

    public SQLink setDbType(DbType dbType)
    {
        this.dbType = dbType;
        return this;
    }

    public SQLink setOption(Option option)
    {
        this.option = option;
        return this;
    }

    public SQLink setFastCreatorFactory(BeanCreatorFactory beanCreatorFactory)
    {
        this.beanCreatorFactory = beanCreatorFactory;
        return this;
    }

    public SQLink addTypeHandler(ITypeHandler<?> iTypeHandler)
    {
        TypeHandlerManager.set(iTypeHandler);
        return this;
    }
}
