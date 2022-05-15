package org.noear.solon.addin;

import org.noear.solon.core.JarClassLoader;
import org.noear.solon.core.PluginEntity;
import org.noear.solon.core.util.PluginUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author noear
 * @since 1.7
 */
public class AddinLoader {
    /**
     * 加载jar插件包
     * */
    public static AddinPackage load(File file) {
        try {
            URL url = file.toURI().toURL();
            JarClassLoader classLoader = new JarClassLoader(JarClassLoader.global());
            classLoader.addJar(url);

            List<PluginEntity> plugins = new ArrayList<>();

            PluginUtil.scanPlugins(classLoader, url.toString(), plugins::add);


            return new AddinPackage(file, classLoader, plugins);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 卸载Jar插件包
     * */
    public static void unload(AddinPackage pluginPackage) {
        try {
            pluginPackage.prestop();
            pluginPackage.stop();

            JarClassLoader classLoader = (JarClassLoader) pluginPackage.getClassLoader();

            classLoader.removeJar(pluginPackage.getFile());
            classLoader.close();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
