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
package org.noear.solon.data.sqlink.plugin.integration;

import org.noear.solon.annotation.Inject;
import org.noear.solon.aot.RuntimeNativeRegistrar;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.data.datasource.DsUtils;
import org.noear.solon.data.sqlink.SqLinkClient;
import org.noear.solon.data.sqlink.base.DbType;
import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.dataSource.DataSourceManager;
import org.noear.solon.data.sqlink.base.session.DefaultSqlSessionFactory;
import org.noear.solon.data.sqlink.base.session.SqlSessionFactory;
import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;
import org.noear.solon.data.sqlink.base.toBean.handler.TypeHandlerManager;
import org.noear.solon.data.sqlink.base.transaction.TransactionManager;
import org.noear.solon.data.sqlink.core.SqLinkBuilder;
import org.noear.solon.data.sqlink.plugin.aot.SqLinkRuntimeNativeRegistrar;
import org.noear.solon.data.sqlink.plugin.builder.AotBeanCreatorFactory;
import org.noear.solon.data.sqlink.plugin.configuration.SqLinkProperties;
import org.noear.solon.data.sqlink.plugin.datasource.SolonDataSourceManager;
import org.noear.solon.data.sqlink.plugin.transaction.SolonTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class XPluginImpl implements Plugin {
    private static final Logger log = LoggerFactory.getLogger(XPluginImpl.class);

    @Override
    public void start(AppContext context) throws Throwable {
        Map<String, Props> data = context.cfg().getGroupedProp("solon.data.sqlink");

        if (data.isEmpty()) {
            return;
        }
        log.info("SQLink启动，共找到{}个配置", data.size());

        // 为每个配置创建一个Client，存到clients
        Map<String, SqLinkClient> clients = new LinkedHashMap<>();
        for (Map.Entry<String, Props> entry : data.entrySet()) {
            Props props = entry.getValue();
            SqLinkProperties properties = props.toBean(SqLinkProperties.class);
            String dsName = entry.getKey();
            if (dsName.isEmpty()) {
                continue;
            }
            DataSourceManager dataSourceManager = new SolonDataSourceManager();
            TransactionManager transactionManager = new SolonTransactionManager(dataSourceManager);
            SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(dataSourceManager, transactionManager);
            AotBeanCreatorFactory aotFastCreatorFactory = new AotBeanCreatorFactory();
            SqLinkClient SQLinkClient = new SqLinkBuilder()
                    .setDataSourceManager(dataSourceManager)
                    .setTransactionManager(transactionManager)
                    .setSqlSessionFactory(sqlSessionFactory)
                    .setBeanCreatorFactory(aotFastCreatorFactory)
                    .setOption(properties.bulidOption())
                    .build();

            //BeanWrap wrap = context.wrap(entry.getKey(), SqLinkClient);
            //context.beanRegister(wrap, entry.getKey(), true);
            clients.put(entry.getKey(), SQLinkClient);
            SqLinkConfig config = SQLinkClient.getConfig();

            DsUtils.observeDs(context, dsName, beanWrap -> registerDataSource(beanWrap, config));
        }

        // 设置注入
        context.beanInjectorAdd(Inject.class, SqLinkClient.class, (varHolder, inject) ->
        {
            varHolder.required(inject.required());
            String name = inject.value();
            // 默认注入第一个
            if (name.isEmpty()) {
                Optional<SqLinkClient> first = clients.values().stream().findFirst();
                varHolder.setValue(first.orElseThrow(RuntimeException::new));
            }
            // 按名称注入
            else {
                varHolder.setValue(clients.get(name));
            }
        });

        // 注册类型处理器
        context.getBeanAsync(ITypeHandler.class, TypeHandlerManager::set);

        // 注册aot
        registerAot(context);
    }

    // 获取数据源并且设置数据库类型
    private static void registerDataSource(BeanWrap beanWrap, SqLinkConfig config) {
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
