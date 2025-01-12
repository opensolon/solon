/*
 * Copyright 2017-2025 noear.org and authors
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

import org.noear.solon.data.sqlink.SqLink;
import org.noear.solon.data.sqlink.base.DbType;
import org.noear.solon.data.sqlink.base.SqLinkDialect;
import org.noear.solon.data.sqlink.base.dataSource.DataSourceManager;
import org.noear.solon.data.sqlink.base.session.DefaultSqlSessionFactory;
import org.noear.solon.data.sqlink.base.session.SqlSessionFactory;
import org.noear.solon.data.sqlink.base.toBean.beancreator.BeanCreatorFactory;
import org.noear.solon.data.sqlink.base.transaction.DefaultTransactionManager;
import org.noear.solon.data.sqlink.base.transaction.TransactionManager;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class SqLinkBuilder {
    /**
     * 方言
     */
    private SqLinkDialect dialect;
    /**
     * 配置
     */
    private Option option = new Option();
    /**
     * 数据源管理器
     */
    private DataSourceManager dataSourceManager;
    /**
     * 事务管理器
     */
    private TransactionManager transactionManager;
    /**
     * 会话工厂
     */
    private SqlSessionFactory sqlSessionFactory;
    /**
     * 对象创建器工厂
     */
    private BeanCreatorFactory beanCreatorFactory;

    /**
     * 构建Client对象
     */
    public SqLink build() {
        if (dataSourceManager == null) {
            throw new NullPointerException("dataSourceManager is null");
        }
        if (transactionManager == null) {
            transactionManager = new DefaultTransactionManager(dataSourceManager);
        }
        if (sqlSessionFactory == null) {
            sqlSessionFactory = new DefaultSqlSessionFactory(dataSourceManager, transactionManager);
        }
        if (beanCreatorFactory == null) {
            beanCreatorFactory = new BeanCreatorFactory();
        }
        Config config = new Config(option, DbType.Any, transactionManager, dataSourceManager, sqlSessionFactory, beanCreatorFactory);
        if (dialect != null) {
            config.setDisambiguation(dialect);
        }
        return new SqLinkImpl(config);
    }

    /**
     * 设置数据源管理器
     */
    public SqLinkBuilder setDataSourceManager(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
        return this;
    }

    /**
     * 设置事务管理器
     */
    public SqLinkBuilder setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        return this;
    }

    /**
     * 设置会话工厂
     */
    public SqLinkBuilder setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        return this;
    }

    /**
     * 设置配置
     */
    public SqLinkBuilder setOption(Option option) {
        this.option = option;
        return this;
    }

    /**
     * 设置对象创建器工厂
     */
    public SqLinkBuilder setBeanCreatorFactory(BeanCreatorFactory beanCreatorFactory) {
        this.beanCreatorFactory = beanCreatorFactory;
        return this;
    }

    /**
     * 设置方言
     */
    public SqLinkBuilder setDialect(SqLinkDialect dialect) {
        this.dialect = dialect;
        return this;
    }
}
