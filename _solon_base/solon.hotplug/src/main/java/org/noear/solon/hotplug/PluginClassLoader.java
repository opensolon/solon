package org.noear.solon.hotplug;

import org.noear.solon.core.JarClassLoader;

/**
 * 插件类加载器
 *
 * @author noear
 * @since 2.2
 */
public class PluginClassLoader extends JarClassLoader {
    public PluginClassLoader(ClassLoader parent){
        super(parent);
    }
}
