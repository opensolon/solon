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
package org.noear.solon.data.sqlink.base;

import org.noear.solon.data.sqlink.api.Aop;
import org.noear.solon.data.sqlink.base.dataSource.DataSourceManager;
import org.noear.solon.data.sqlink.base.expression.SqlExpressionFactory;
import org.noear.solon.data.sqlink.base.session.SqlSessionFactory;
import org.noear.solon.data.sqlink.base.toBean.Include.IncludeFactory;
import org.noear.solon.data.sqlink.base.toBean.beancreator.BeanCreatorFactory;
import org.noear.solon.data.sqlink.base.transaction.TransactionManager;

/**
 * 运行时配置
 *
 * @author kiryu1223
 * @since 3.0
 */
public interface SqLinkConfig {
    /**
     * 获取表达式工厂
     */
    SqlExpressionFactory getSqlExpressionFactory();

    /**
     * 获取数据库方言
     */
    SqLinkDialect getDisambiguation();

    /**
     * 设置数据库方言
     */
    void setDisambiguation(SqLinkDialect disambiguation);

    /**
     * 获取数据源管理器
     */
    DataSourceManager getDataSourceManager();

    /**
     * 获取事务管理器
     */
    TransactionManager getTransactionManager();

    /**
     * 获取会话工厂
     */
    SqlSessionFactory getSqlSessionFactory();

    /**
     * 获取抓取器工厂
     */
    IncludeFactory getIncludeFactory();

    /**
     * 获取对象创建器工厂
     */
    BeanCreatorFactory getBeanCreatorFactory();

    /**
     * 获取拦截器
     */
    Aop getAop();

    /**
     * 设置数据库类型
     */
    void setDbType(DbType dbType);

    /**
     * 获取数据库类型
     */
    DbType getDbType();

    /**
     * 是否打印SQL
     */
    boolean isPrintSql();

    /**
     * 是否打印批量SQL
     */
    boolean isPrintBatch();

    /**
     * 是否忽略没有where的删除
     */
    boolean isIgnoreDeleteNoWhere();

    /**
     * 是否忽略没有where的更新
     */
    boolean isIgnoreUpdateNoWhere();
}
