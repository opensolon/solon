package org.noear.solon.hotplug;

import org.noear.solon.Solon;
import org.noear.solon.core.*;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.ResourceUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
    /**
     * Aop 上下文
     */
    private AppContext context;

    private final ReentrantLock SYNC_LOCK = new ReentrantLock();

    public PluginPackage(File file, PluginClassLoader classLoader, List<PluginEntity> plugins) {
        this.file = file;
        this.plugins = plugins;
        this.classLoader = classLoader;
        this.context = new AppContext(classLoader, new Props(classLoader));

        Solon.context().copyTo(this.context);

        if (plugins.size() > 0) {
            //进行优先级顺排（数值要倒排）
            //
            plugins.sort(Comparator.comparingInt(PluginEntity::getPriority).reversed());
        }
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
                p1.init(context);
            }

            for (PluginEntity p1 : plugins) {
                p1.start(context);
            }

            context.start();
            started = true;

            return this;
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    /**
     * 预停止插件包
     */
    public void prestop() {
        SYNC_LOCK.lock();
        try {
            started = false;
            for (PluginEntity p1 : plugins) {
                p1.prestop();
            }
            context.prestop();
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
            context.stop();
            context.clear();
        } finally {
            SYNC_LOCK.unlock();
        }
    }
}
