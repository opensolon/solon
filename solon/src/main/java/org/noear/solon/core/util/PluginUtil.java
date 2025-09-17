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
package org.noear.solon.core.util;

import org.noear.solon.Utils;
import org.noear.solon.core.PluginEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Collection;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * 插件工具
 *
 * @author noear
 * @since 1.7
 */
public class PluginUtil {
    static final Logger log = LoggerFactory.getLogger(PluginUtil.class);

    /**
     * 扫描插件
     *
     * @param classLoader 类加载器
     * @param excludeList 排除列表
     */
    public static void scanPlugins(ClassLoader classLoader, Collection<String> excludeList, Consumer<PluginEntity> consumer) {
        //3.查找插件配置（如果出错，让它抛出异常）
        Collection<String> nameList = ScanUtil.scan(classLoader, "META-INF/solon", n -> n.endsWith(".properties"));

        for (String name : nameList) {
            URL res = ResourceUtil.getResource(classLoader, name);

            if (res == null) {
                // native 时，扫描出来的resource可能是不存在的（这种情况就是bug），需要给于用户提示，反馈给社区
                log.warn("Solon plugin: name=" + name + ", resource is null");
            } else {
                Properties props = Utils.loadProperties(res);
                findPlugins(classLoader, props, excludeList, consumer);
            }
        }
    }

    /**
     * 查找插件
     */
    public static void findPlugins(ClassLoader classLoader, Properties props, Collection<String> excludeList,Consumer<PluginEntity> consumer) {
        String pluginStr = props.getProperty("solon.plugin");

        if (Utils.isNotEmpty(pluginStr)) {
            String priorityStr = props.getProperty("solon.plugin.priority");
            int priority = 0;
            if (Utils.isNotEmpty(priorityStr)) {
                priority = Integer.parseInt(priorityStr);
            }

            String[] plugins = pluginStr.trim().split(",");

            for (String clzName : plugins) {
                if (clzName.length() > 0) {
                    if(excludeList.contains(clzName)) {
                        continue;
                    }

                    PluginEntity ent = new PluginEntity(classLoader, clzName.trim(), props);
                    ent.setPriority(priority);
                    consumer.accept(ent);
                }
            }
        }
    }
}