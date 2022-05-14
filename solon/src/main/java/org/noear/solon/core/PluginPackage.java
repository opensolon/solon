package org.noear.solon.core;

import org.noear.solon.SolonApp;

import java.io.File;
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

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public File getFile() {
        return file;
    }

    public void start(SolonApp app) {
        for (PluginEntity p1 : plugins) {
            p1.start();
        }
    }

    public void prestop() {
        for (PluginEntity p1 : plugins) {
            p1.prestop();
        }
    }

    public void stop() {
        for (PluginEntity p1 : plugins) {
            p1.stop();
        }
    }
}
