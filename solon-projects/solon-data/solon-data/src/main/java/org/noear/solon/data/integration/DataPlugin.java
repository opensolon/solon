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
package org.noear.solon.data.integration;

import org.noear.solon.core.*;
import org.noear.solon.core.event.AppPluginLoadEndEvent;
import org.noear.solon.data.annotation.*;
import org.noear.solon.data.datasource.DsBuilder;
import org.noear.solon.data.datasource.DsInjector;
import org.noear.solon.data.datasource.RoutingDataSource;
import org.noear.solon.data.tran.TranManager;
import org.noear.solon.data.tran.interceptor.TranInterceptor;
import org.noear.solon.data.tran.interceptor.TransactionInterceptor;

/**
 * @author noear
 */
public class DataPlugin implements Plugin {
    @Override
    public void start(AppContext context) {

        //添加事务控制支持
        if (context.app().enableTransaction()) {
            //添加数据源路由记录
            TranManager.routing(RoutingDataSource.class, (r) -> r.determineCurrentTarget());

            //添加注解支持
            context.beanInterceptorAdd(Tran.class, TranInterceptor.instance, 120);
            context.beanInterceptorAdd(Transaction.class, TransactionInterceptor.instance, 120);
        }

        //@since 3.2
        context.beanInjectorAdd(Ds.class, DsInjector.getDefault());
        context.beanBuilderAdd(Ds.class, DsBuilder.getDefault());

        //自动构建数据源
        context.app().onEvent(AppPluginLoadEndEvent.class, new DataSourcesBuilder());
    }
}