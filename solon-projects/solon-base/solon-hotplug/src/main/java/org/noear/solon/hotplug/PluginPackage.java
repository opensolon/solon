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
import org.noear.solon.core.*;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.PluginUtil;
import org.noear.solon.core.util.ResourceUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 外接小程序包
 *
 * @author noear
 * @since 1.8
 */
public class PluginPackage {
    /**
     * 包文件
     */
    private final File file;
    /**
     * 类加载器
     */
    private final PluginClassLoader classLoader;
    /**
     * 找到的插件
     */
    private final List<PluginEntity> plugins;
    /**
     * 开始状态
     */
    private boolean started;

    private final ReentrantLock SYNC_LOCK = new ReentrantLock();

    public PluginPackage(File file, PluginClassLoader classLoader, List<PluginEntity> plugins) {
        this.file = file;
        this.plugins = plugins;
        this.classLoader = classLoader;

        if (plugins.size() > 0) {
            //进行优先级顺排（数值要倒排）
            //
            plugins.sort(Comparator.comparingInt(PluginEntity::getPriority).reversed());
        }
    }


    /**
     * 插件上下文
     */
    private AppContext context;

    private AppContext getContext() {
        if (context == null) {
            this.context = new AppContext(classLoader, new Props(classLoader));
            Solon.context().copyTo(this.context);
        }

        return context;
    }

    public File getFile() {
        return file;
    }

    public PluginClassLoader getClassLoader() {
        return classLoader;
    }

    public boolean getStarted() {
        return started;
    }


    public Class<?> loadClass(String className) {
        return ClassUtil.loadClass(getClassLoader(), className);
    }

    public <T> T tryInstance(String className) {
        return ClassUtil.tryInstance(getClassLoader(), className);
    }

    public URL getResource(String name) {
        return ResourceUtil.getResource(getClassLoader(), name);
    }

    public String getResourceAsString(String name) throws IOException {
        return ResourceUtil.getResourceAsString(getClassLoader(), name, Solon.encoding());
    }

    public String getResourceAsString(String name, String charset) throws IOException {
        return ResourceUtil.getResourceAsString(getClassLoader(), name, charset);
    }

    /**
     * 启动插件包
     */
    public PluginPackage start() {
        SYNC_LOCK.lock();
        try {
            for (PluginEntity p1 : plugins) {
                p1.init(getContext());
            }

            for (PluginEntity p1 : plugins) {
                p1.start(getContext());
            }

            getContext().start();
            started = true;

            return this;
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    /**
     * 预停止插件包
     *
     * @deprecated 3.7 {{@link #preStop()}}
     */
    @Deprecated
    public void prestop() {
        preStop();
    }

    /**
     * 预停止插件包
     *
     * @since 3.7
     */
    public void preStop() {
        SYNC_LOCK.lock();
        try {
            started = false;
            for (PluginEntity p1 : plugins) {
                p1.preStop();
            }

            if (context != null) {
                getContext().preStop();
            }

        } finally {
            SYNC_LOCK.unlock();
        }
    }

    /**
     * 停止插件包
     */
    public void stop() {
        SYNC_LOCK.lock();
        try {
            started = false;
            for (PluginEntity p1 : plugins) {
                p1.stop();
            }

            if (context != null) {
                getContext().stop();
                getContext().clear();
                context = null;
            }
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    ///////////////////////////////////////////////////////////////
    // 静态方法 （放 PluginManager 那儿容易让人误用）
    //////////////////////////////////////////////////////////////

    /**
     * 加载 jar 插件包
     *
     * @param file 文件
     */
    public static PluginPackage loadJar(File file) {
        try {
            URL url = file.toURI().toURL();
            PluginClassLoader classLoader = new PluginClassLoader(AppClassLoader.global());
            classLoader.addJar(url);

            List<PluginEntity> plugins = new ArrayList<>();

            PluginUtil.scanPlugins(classLoader, Collections.emptyList(), plugins::add);


            return new PluginPackage(file, classLoader, plugins);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 卸载 Jar 插件包
     *
     * @param pluginPackage 插件包
     */
    public static void unloadJar(PluginPackage pluginPackage) {
        try {
            if (pluginPackage.getStarted()) {
                pluginPackage.preStop();
                pluginPackage.stop();
            }

            PluginClassLoader classLoader = pluginPackage.getClassLoader();

            classLoader.removeJar(pluginPackage.getFile());
            classLoader.close();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
