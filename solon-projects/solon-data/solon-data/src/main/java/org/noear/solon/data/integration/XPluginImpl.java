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
import org.noear.solon.data.cache.*;
import org.noear.solon.data.cache.interceptor.CacheInterceptor;
import org.noear.solon.data.cache.interceptor.CachePutInterceptor;
import org.noear.solon.data.cache.interceptor.CacheRemoveInterceptor;
import org.noear.solon.data.datasource.DsUtils;
import org.noear.solon.data.datasource.R2dbcConnectionFactory;
import org.noear.solon.data.datasource.RoutingDataSource;
import org.noear.solon.data.tran.TranManager;
import org.noear.solon.data.tran.interceptor.TranInterceptor;
import org.noear.solon.vault.VaultUtils;

import javax.sql.DataSource;
import java.util.Map;

public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) {
        //注册缓存工厂
        CacheLib.cacheFactoryAdd("local", new LocalCacheFactoryImpl());

        //添加事务控制支持
        if (context.app().enableTransaction()) {
            //添加数据源路由记录
            TranManager.routing(RoutingDataSource.class, (r) -> r.determineCurrentTarget());

            //添加注解支持
            context.beanInterceptorAdd(Tran.class, TranInterceptor.instance, 120);
        }

        //添加缓存控制支持
        if (context.app().enableCaching()) {
            CacheLib.cacheServiceAddIfAbsent("", LocalCacheService.instance);

            context.subWrapsOfType(CacheService.class, new CacheServiceWrapConsumer());

            context.lifecycle(Constants.LF_IDX_PLUGIN_BEAN_USES, () -> {
                if (context.hasWrap(CacheService.class) == false) {
                    context.wrapAndPut(CacheService.class, LocalCacheService.instance);
                }
            });

            context.beanInterceptorAdd(CachePut.class, new CachePutInterceptor(), 110);
            context.beanInterceptorAdd(CacheRemove.class, new CacheRemoveInterceptor(), 110);
            context.beanInterceptorAdd(Cache.class, new CacheInterceptor(), 111);
        }

        //自动构建数据源
        context.app().onEvent(AppPluginLoadEndEvent.class, e -> {
            //不能提前获取，否则默认表达式或许未完成
            Props props = context.cfg().getProp("solon.dataSources");
            if (props.size() > 0) {
                //支持 ENC() 加密符
                VaultUtils.guard(props);
                buildDataSource(context, props);
            }
        });
    }

    private void buildDataSource(AppContext context, Props props) {
        Map<String, DataSource> dsmap = DsUtils.buildDsMap(props);

        if (dsmap.size() > 0) {
            for (Map.Entry<String, DataSource> kv : dsmap.entrySet()) {
                boolean typed = false;
                String name = kv.getKey();
                if (name.endsWith("!")) {
                    name = name.substring(0, name.length() - 1);
                    typed = true;
                }

                if (kv.getValue() instanceof R2dbcConnectionFactory) {
                    ((R2dbcConnectionFactory) kv.getValue()).register(context, name, typed);
                } else {
                    BeanWrap dsBw = context.wrap(name, kv.getValue(), typed);

                    //按名字注册
                    context.putWrap(name, dsBw);
                    if (typed) {
                        //按类型注册
                        context.putWrap(DataSource.class, dsBw);
                    }
                    //对外发布
                    context.wrapPublish(dsBw);

                    //aot注册
                    context.aot().registerEntityType(dsBw.rawClz(), null);
                }
            }
        }
    }
}