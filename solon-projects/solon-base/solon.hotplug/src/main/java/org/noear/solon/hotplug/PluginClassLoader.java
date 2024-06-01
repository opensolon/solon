package org.noear.solon.hotplug;

import org.noear.solon.core.AppClassLoader;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

/**
 * 插件类加载器
 *
 * @author noear
 * @since 2.2
 */
public class PluginClassLoader extends AppClassLoader {
    public PluginClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        //只获取本级资源
        return findResources(name);
    }

    @Override
    public URL getResource(String name) {
        //只获取本级资源
        return findResource(name);
    }
}
