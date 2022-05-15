package org.noear.solon.core;

import org.noear.solon.Solon;
import org.noear.solon.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

/**
 * 插件包
 *
 * @author noear
 * @since 1.7
 */
public class PluginPackage {
    private final File file;
    private final JarClassLoader classLoader;
    private final List<PluginEntity> plugins;

    public PluginPackage(File file, JarClassLoader classLoader, List<PluginEntity> plugins) {
        if (plugins.size() > 0) {
            //进行优先级顺排（数值要倒排）
            //
            plugins.sort(Comparator.comparingInt(PluginEntity::getPriority).reversed());
        }

        this.file = file;
        this.plugins = plugins;
        this.classLoader = classLoader;
    }

    public File getFile() {
        return file;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
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
    public PluginPackage start() {
        for (PluginEntity p1 : plugins) {
            p1.start();
        }

        return this;
    }

    /**
     * 预停止插件包
     */
    public PluginPackage prestop() {
        for (PluginEntity p1 : plugins) {
            p1.prestop();
        }

        return this;
    }

    /**
     * 停止插件包
     */
    public PluginPackage stop() {
        for (PluginEntity p1 : plugins) {
            p1.stop();
        }

        return this;
    }
}
