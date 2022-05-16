package org.noear.solon.pluginplus;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.JarClassLoader;
import org.noear.solon.core.PluginEntity;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

/**
 * 外接小程序包
 *
 * @author noear
 * @since 1.7
 */
public class PluginPackage {
    private final File file;
    private final JarClassLoader classLoader;
    private final List<PluginEntity> plugins;
    private boolean started;

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

    public boolean getStarted(){
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
            p1.start();
        }
        started = true;

        return this;
    }

    /**
     * 预停止插件包
     */
    public synchronized PluginPackage prestop() {
        for (PluginEntity p1 : plugins) {
            p1.prestop();
        }
        started = false;

        return this;
    }

    /**
     * 停止插件包
     */
    public synchronized PluginPackage stop() {
        for (PluginEntity p1 : plugins) {
            p1.stop();
        }
        started = false;

        return this;
    }
}
