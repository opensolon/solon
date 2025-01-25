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
package org.noear.solon.data.sqlink.integration;

import org.noear.solon.annotation.Inject;
import org.noear.solon.aot.RuntimeNativeRegistrar;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.data.datasource.DsUtils;
import org.noear.solon.data.sqlink.SqLink;
import org.noear.solon.data.sqlink.base.DbType;
import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.dataSource.DataSourceManager;
import org.noear.solon.data.sqlink.base.session.DefaultSqlSessionFactory;
import org.noear.solon.data.sqlink.base.session.SqlSessionFactory;
import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;
import org.noear.solon.data.sqlink.base.toBean.handler.TypeHandlerManager;
import org.noear.solon.data.sqlink.base.transaction.TransactionManager;
import org.noear.solon.data.sqlink.core.SqLinkBuilder;
import org.noear.solon.data.sqlink.integration.aot.SqLinkRuntimeNativeRegistrar;
import org.noear.solon.data.sqlink.integration.builder.AotBeanCreatorFactory;
import org.noear.solon.data.sqlink.integration.configuration.SqLinkProperties;
import org.noear.solon.data.sqlink.integration.datasource.SolonDataSourceManager;
import org.noear.solon.data.sqlink.integration.transaction.SolonTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class SqLinkPlugin implements Plugin {
    private static final Logger log = LoggerFactory.getLogger(SqLinkPlugin.class);

    // 为每个配置创建一个Client，存到clients
    private Map<String, SqLink> clients = new LinkedHashMap<>();

    @Override
    public void start(AppContext context) throws Throwable {
        // 获取配置（允许没有。即，可默认）
        Map<String, Props> data = context.cfg().getGroupedProp("solon.data.sqlink");

        // 订阅数据源（相对之前方式，它支持动态扩充）
        context.subWrapsOfType(DataSource.class, dsBw -> {
            String dsName = dsBw.name();
            Props dsProps = data.get(dsName);

            SqLinkProperties properties;
            if (dsProps == null) {
                properties = new SqLinkProperties(); //允许没有配置（即，可默认）
            }
            else {
                properties = dsProps.toBean(SqLinkProperties.class);
            }

            DataSourceManager dataSourceManager = new SolonDataSourceManager();
            TransactionManager transactionManager = new SolonTransactionManager(dataSourceManager);
            SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(dataSourceManager, transactionManager);
            AotBeanCreatorFactory aotFastCreatorFactory = new AotBeanCreatorFactory();

            SqLink sqLink = new SqLinkBuilder()
                    .setDataSourceManager(dataSourceManager)
                    .setTransactionManager(transactionManager)
                    .setSqlSessionFactory(sqlSessionFactory)
                    .setBeanCreatorFactory(aotFastCreatorFactory)
                    .setOption(properties.bulidOption())
                    .build();

            clients.put(dsName, sqLink);
            SqLinkConfig config = sqLink.getConfig();

            DsUtils.observeDs(context, dsName, beanWrap -> registerDataSource(beanWrap, config));

        });


        // 设置注入（相对之前方式，它支持动态扩充）
        context.beanInjectorAdd(Inject.class, SqLink.class, (varHolder, inject) -> {
            varHolder.required(inject.required());
            String dsName = inject.value();

            //确保有数据源存在（）
            DsUtils.observeDs(context, dsName, dsBw -> {
                SqLink sqLink = clients.get(dsBw.name());
                varHolder.setValue(sqLink);
            });
        });

        // 注册类型处理器
        context.getBeanAsync(ITypeHandler.class, TypeHandlerManager::set);

        // 注册aot
        registerAot(context);
    }

    // 获取数据源并且设置数据库类型
    private void registerDataSource(BeanWrap beanWrap, SqLinkConfig config) {
        SolonDataSourceManager solonDataSourceManager = (SolonDataSourceManager) config.getDataSourceManager();
        solonDataSourceManager.setDataSource(beanWrap.get());

        try (Connection connection = solonDataSourceManager.getConnection()) {
            String databaseProductName = connection.getMetaData().getDatabaseProductName();
            DbType dbType = DbType.getByName(databaseProductName);
            config.setDbType(dbType);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 注册aot
    private void registerAot(AppContext context) {
        if (NativeDetector.isAotRuntime() && ClassUtil.hasClass(() -> RuntimeNativeRegistrar.class)) {
            context.wrapAndPut(SqLinkRuntimeNativeRegistrar.class, new SqLinkRuntimeNativeRegistrar());
        }
    }
}