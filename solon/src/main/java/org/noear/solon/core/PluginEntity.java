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
package org.noear.solon.core;

import org.noear.solon.SolonProps;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.lang.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * 插件实体
 *
 * @see SolonProps#pluginScan(List)
 * @author noear
 * @since 1.0
 * */
public class PluginEntity implements Comparable<PluginEntity> {
    static final Logger log = LoggerFactory.getLogger(PluginEntity.class);

    /**
     * 类名（全路径）
     */
    private String className;
    /**
     * 类加载器
     */
    private ClassLoader classLoader;
    /**
     * 优先级（大的优先）
     */
    private int priority = 0;
    /**
     * 插件
     */
    private Plugin plugin;

    /**
     * 插件属性（申明插件类与优先级）
     */
    private Properties props;

    public PluginEntity(ClassLoader classLoader, String className, Properties props) {
        this.classLoader = classLoader;
        this.className = className;
        this.props = props;
    }

    public PluginEntity(Plugin plugin) {
        this.plugin = plugin;
    }

    public PluginEntity(Plugin plugin, int priority) {
        this.plugin = plugin;
        this.priority = priority;
    }


    /**
     * 获取优先级
     */
    public int getPriority() {
        return priority;
    }

    /**
     * 设置优先级
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * 获取插件
     */
    public @Nullable Plugin getPlugin() {
        return plugin;
    }

    public Properties getProps() {
        return props;
    }

    public String getClassName() {
        return className;
    }

    /**
     * 初始化
     */
    public void init(AppContext context) {
        initInstance(context);
    }

    /**
     * 启动
     */
    public void start(AppContext context) {
        if (plugin != null) {
            try {
                plugin.start(context);
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                throw new IllegalStateException("The plugin start failed: " + className, e);
            }
        }
    }

    /**
     * 预停止
     *
     * @deprecated 3.7 {@link #preStop()}
     */
    @Deprecated
    public void prestop() {
        preStop();
    }

    /**
     * 预停止
     *
     * @since 3.7
     */
    public void preStop() {
        if (plugin != null) {
            try {
                plugin.preStop(); //新名
            } catch (Throwable e) {
                log.warn("The plugin preStop failed: {}", className, e);
            }
        }
    }

    /**
     * 停止
     */
    public void stop() {
        if (plugin != null) {
            try {
                plugin.stop();
            } catch (Throwable e) {
                log.warn("The plugin stop failed: {}", className, e);
            }
        }
    }

    /**
     * 初始化
     */
    private void initInstance(AppContext context) {
        if (plugin == null) {
            if (classLoader != null) {
                plugin = ClassUtil.tryInstance(classLoader, className);

                if (plugin == null) {
                    log.warn("The configured plugin cannot load: " + className);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "PluginEntity{" +
                "priority=" + priority +
                ", className='" + className + '\'' +
                '}';
    }

    @Override
    public int compareTo(PluginEntity o) {
        if (this.priority == o.priority) {
            return 0;
        } else if (this.priority > o.priority) { //越大越优
            return -1;
        } else {
            return 1;
        }
    }
}
