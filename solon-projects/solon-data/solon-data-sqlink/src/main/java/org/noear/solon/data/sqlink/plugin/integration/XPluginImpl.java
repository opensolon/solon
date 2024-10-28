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

import org.noear.solon.aot.RuntimeNativeRegistrar;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.data.datasource.DsUtils;
import org.noear.solon.data.sqlink.base.dataSource.DataSourceManager;
import org.noear.solon.data.sqlink.base.session.DefaultSqlSessionFactory;
import org.noear.solon.data.sqlink.base.session.SqlSessionFactory;
import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;
import org.noear.solon.data.sqlink.base.toBean.handler.TypeHandlerManager;
import org.noear.solon.data.sqlink.base.transaction.TransactionManager;
import org.noear.solon.data.sqlink.core.SQLink;
import org.noear.solon.data.sqlink.core.api.client.Client;
import org.noear.solon.data.sqlink.plugin.aot.SQLinkRuntimeNativeRegistrar;
import org.noear.solon.data.sqlink.plugin.builder.AotBeanCreatorFactory;
import org.noear.solon.data.sqlink.plugin.configuration.SQLinkProperties;
import org.noear.solon.data.sqlink.plugin.datasource.SolonDataSourceManager;
import org.noear.solon.data.sqlink.plugin.datasource.SolonDataSourceManagerWrap;
import org.noear.solon.data.sqlink.plugin.transaction.SolonTransactionManager;

import java.util.Map;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class XPluginImpl implements Plugin
{
    @Override
    public void start(AppContext context) throws Throwable
    {
        Map<String, Props> data = context.cfg().getGroupedProp("solon.data.SQLink");
        if (data.isEmpty()) return;
        for (Map.Entry<String, Props> entry : data.entrySet())
        {
            Props props = entry.getValue();
            SQLinkProperties properties = props.toBean(SQLinkProperties.class);
            String dsName = properties.getDsName();
            if (dsName.isEmpty()) continue;
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

            BeanWrap wrap = context.wrap(entry.getKey(), client);
            context.beanRegister(wrap, entry.getKey(), true);

            DsUtils.observeDs(context, dsName, beanWrap ->
            {
                SolonDataSourceManagerWrap sourceManagerWrap = (SolonDataSourceManagerWrap) client.getConfig().getDataSourceManager();
                sourceManagerWrap.setDataSourceManager(new SolonDataSourceManager(beanWrap.get()));
            });
        }

        context.getBeanAsync(ITypeHandler.class, TypeHandlerManager::set);
        registerAot(context);
    }

    private void registerAot(AppContext context)
    {
        if (NativeDetector.isAotRuntime() && ClassUtil.hasClass(() -> RuntimeNativeRegistrar.class))
        {
            context.wrapAndPut(SQLinkRuntimeNativeRegistrar.class, new SQLinkRuntimeNativeRegistrar());
        }
    }
}
