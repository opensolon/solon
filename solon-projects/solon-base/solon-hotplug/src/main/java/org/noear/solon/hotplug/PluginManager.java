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
package org.noear.solon.hotplug;

import org.noear.solon.Solon;

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
                    add((String) k, new File((String) v));
                }
            });
        }
    }

    public static Collection<PluginInfo> getPlugins() {
        return pluginMap.values();
    }

    public static void add(String name, File file) {
        pluginMap.computeIfAbsent(name, k -> {
            return new PluginInfo(name, file);
        });
    }

    public static void remove(String name) {
        pluginMap.remove(name);
    }


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