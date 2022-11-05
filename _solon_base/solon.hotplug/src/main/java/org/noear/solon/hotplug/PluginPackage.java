package org.noear.solon.hotplug;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

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
    private final JarClassLoader classLoader;
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
    private AopContext context;

    public PluginPackage(File file, JarClassLoader classLoader, List<PluginEntity> plugins) {
        this.file = file;
        this.plugins = plugins;
        this.classLoader = classLoader;
        this.context = Solon.context().copy(classLoader, new Props(classLoader));

        if (plugins.size() > 0) {
            //进行优先级顺排（数值要倒排）
            //
            plugins.sort(Comparator.comparingInt(PluginEntity::getPriority).reversed());
        }
    }

    public File getFile() {
        return file;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public boolean getStarted() {
        return started;
    }


    public Class<?> loadClass(String className) {
        return Utils.loadClass(getClassLoader(), className);
    }

    public <T> T newInstance(String className) {
        return Utils.newInstance(getClassLoader(), className);
    }

    public URL getResource(String name) {
        return Utils.getResource(getClassLoader(), name);
    }

    public String getResourceAsString(String name) throws IOException {
        return Utils.getResourceAsString(getClassLoader(), name, Solon.encoding());
    }

    public String getResourceAsString(String name, String charset) throws IOException {
        return Utils.getResourceAsString(getClassLoader(), name, charset);
    }

    /**
     * 启动插件包
     */
    public synchronized PluginPackage start() {
        for (PluginEntity p1 : plugins) {
            p1.init();
        }

        for (PluginEntity p1 : plugins) {
            p1.start(context);
        }
        context.beanLoaded();
        started = true;

        return this;
    }

    /**
     * 预停止插件包
     */
    public synchronized void prestop() {
        for (PluginEntity p1 : plugins) {
            p1.prestop();
        }
        started = false;
    }

    /**
     * 停止插件包
     */
    public synchronized void stop() {
        for (PluginEntity p1 : plugins) {
            p1.stop();
        }
        context.clear();
        started = false;
    }
}
