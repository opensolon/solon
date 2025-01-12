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
package org.noear.solon.hotplug;

import org.noear.solon.Solon;
import org.noear.solon.Utils;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 外接小程序管理器
 *
 * @author noear
 * @since 1.8
 */
public class PluginManager {
    static final Map<String, PluginInfo> pluginMap = new ConcurrentHashMap<>();

    static {
        Properties pops = Solon.cfg().getProp("solon.hotplug");

        if (pops.size() > 0) {
            pops.forEach((k, v) -> {
                if (k instanceof String && v instanceof String) {
                    //通过 Utils.getFile 获取文件，支持 ./xxx.jar 或者 xxx.jar （相对主程序包的位置）
                    add((String) k, Utils.getFile((String) v));
                }
            });
        }
    }

    public static Collection<PluginInfo> getPlugins() {
        return pluginMap.values();
    }

    /**
     * 添加插件
     *
     * @param name 插件名
     * @param file 插件的 jar 文件
     */
    public static void add(String name, File file) {
        pluginMap.computeIfAbsent(name, k -> {
            return new PluginInfo(name, file);
        });
    }

    /**
     * 移除插件
     *
     * @param name 插件名
     */
    public static void remove(String name) {
        pluginMap.remove(name);
    }


    /**
     * 加载插件
     *
     * @param name 插件名
     */
    public static PluginPackage load(String name) {
        PluginInfo info = pluginMap.get(name);

        if (info == null) {
            throw new IllegalArgumentException("Addin does not exist: " + name);
        }

        if (info.getAddinPackage() == null) {
            info.setAddinPackage(PluginPackage.loadJar(info.getFile()));
        }

        return info.getAddinPackage();
    }

    /**
     * 卸载插件
     *
     * @param name 插件名
     */
    public static void unload(String name) {
        PluginInfo info = pluginMap.get(name);

        if (info == null) {
            throw new IllegalArgumentException("Addin does not exist: " + name);
        }

        if (info.getAddinPackage() == null) {
            return;
        }

        PluginPackage.unloadJar(info.getAddinPackage());
        info.setAddinPackage(null);
    }

    /**
     * 启动插件
     *
     * @param name 插件名
     */
    public static void start(String name) {
        PluginInfo info = pluginMap.get(name);

        if (info == null) {
            throw new IllegalArgumentException("Addin does not exist: " + name);
        }

        if (info.getAddinPackage() == null) {
            //如果未加载，则自动加载
            info.setAddinPackage(PluginPackage.loadJar(info.getFile()));
        }

        if (info.getStarted()) {
            return;
        }

        info.getAddinPackage().start();
    }

    /**
     * 停止插件
     *
     * @param name 插件名
     */
    public static void stop(String name) {
        PluginInfo info = pluginMap.get(name);

        if (info == null) {
            throw new IllegalArgumentException("Addin does not exist: " + name);
        }

        if (info.getStarted() == false) {
            return;
        }

        if (info.getAddinPackage() != null) {
            info.getAddinPackage().prestop();
            info.getAddinPackage().stop();
        }
    }
}