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
import org.noear.solon.data.sqlink.base.DbType;
import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.dataSource.DataSourceManager;
import org.noear.solon.data.sqlink.base.session.DefaultSqlSessionFactory;
import org.noear.solon.data.sqlink.base.session.SqlSessionFactory;
import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;
import org.noear.solon.data.sqlink.base.toBean.handler.TypeHandlerManager;
import org.noear.solon.data.sqlink.base.transaction.TransactionManager;
import org.noear.solon.data.sqlink.core.SQLink;
import org.noear.solon.data.sqlink.core.api.Client;
import org.noear.solon.data.sqlink.plugin.aot.SQLinkRuntimeNativeRegistrar;
import org.noear.solon.data.sqlink.plugin.builder.AotBeanCreatorFactory;
import org.noear.solon.data.sqlink.plugin.configuration.SQLinkProperties;
import org.noear.solon.data.sqlink.plugin.datasource.SolonDataSourceManager;
import org.noear.solon.data.sqlink.plugin.datasource.SolonDataSourceManagerWrap;
import org.noear.solon.data.sqlink.plugin.transaction.SolonTransactionManager;

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
    @Override
    public void start(AppContext context) throws Throwable {
        System.out.println("SQLink启动");
        Map<String, Props> data = context.cfg().getGroupedProp("solon.data.sqlink");
        if (data.isEmpty()) {
            return;
        }
        Map<String, Client> clients = new LinkedHashMap<>();
        for (Map.Entry<String, Props> entry : data.entrySet()) {
            Props props = entry.getValue();
            SQLinkProperties properties = props.toBean(SQLinkProperties.class);
            String dsName = entry.getKey();
            if (dsName.isEmpty()) {
                continue;
            }
            DataSourceManager dataSourceManager = new SolonDataSourceManagerWrap();
            TransactionManager transactionManager = new SolonTransactionManager(dataSourceManager);
            SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(dataSourceManager, transactionManager);
            AotBeanCreatorFactory aotFastCreatorFactory = new AotBeanCreatorFactory();
            Client client = SQLink.bootStrap()
                    .setDataSourceManager(dataSourceManager)
                    .setTransactionManager(transactionManager)
                    .setSqlSessionFactory(sqlSessionFactory)
                    .setFastCreatorFactory(aotFastCreatorFactory)
                    .setOption(properties.bulidOption())
                    .build();

            //BeanWrap wrap = context.wrap(entry.getKey(), client);
            //context.beanRegister(wrap, entry.getKey(), true);
            clients.put(entry.getKey(), client);
            IConfig config = client.getConfig();

            DsUtils.observeDs(context, dsName, beanWrap -> registerDataSource(beanWrap, config));
        }

        context.beanInjectorAdd(Inject.class, Client.class, (varHolder, inject) ->
        {
            varHolder.required(inject.required());
            String name = inject.value();
            if (name.isEmpty()) {
                Optional<Client> first = clients.values().stream().findFirst();
                varHolder.setValue(first.orElseThrow(RuntimeException::new));
            }
            else {
                varHolder.setValue(clients.get(name));
            }
        });

        context.getBeanAsync(ITypeHandler.class, TypeHandlerManager::set);
        registerAot(context);
    }

    private static void registerDataSource(BeanWrap beanWrap, IConfig config) {
        SolonDataSourceManagerWrap sourceManagerWrap = (SolonDataSourceManagerWrap) config.getDataSourceManager();
        sourceManagerWrap.setDataSourceManager(new SolonDataSourceManager(beanWrap.get()));
        try (Connection connection = sourceManagerWrap.getConnection()) {
            String databaseProductName = connection.getMetaData().getDatabaseProductName();
            DbType dbType = DbType.getByName(databaseProductName);
            config.setDbType(dbType);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerAot(AppContext context) {
        if (NativeDetector.isAotRuntime() && ClassUtil.hasClass(() -> RuntimeNativeRegistrar.class)) {
            context.wrapAndPut(SQLinkRuntimeNativeRegistrar.class, new SQLinkRuntimeNativeRegistrar());
        }
    }
}
