package org.noear.solon.hotplug;

import org.noear.solon.Solon;
import org.noear.solon.core.AppClassLoader;
import org.noear.solon.core.PluginEntity;
import org.noear.solon.core.util.PluginUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
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

    public static Collection<PluginInfo> getPlugins(){
        return pluginMap.values();
    }

    public static void add(String name, File file) {
        pluginMap.computeIfAbsent(name, k -> {
            return new PluginInfo(name, file);
        });
    }

    public static void remove(String name){
        pluginMap.remove(name);
    }


    public static PluginPackage load(String name) {
        PluginInfo info = pluginMap.get(name);

        if (info == null) {
            throw new IllegalArgumentException("Addin does not exist: " + name);
        }

        if (info.getAddinPackage() == null) {
            info.setAddinPackage(loadJar(info.getFile()));
        }

        return info.getAddinPackage();
    }

    public static void unload(String name){
        PluginInfo info = pluginMap.get(name);

        if (info == null) {
            throw new IllegalArgumentException("Addin does not exist: " + name);
        }

        if(info.getAddinPackage() == null){
            return;
        }

        unloadJar(info.getAddinPackage());
        info.setAddinPackage(null);
    }

    public static void start(String name) {
        PluginInfo info = pluginMap.get(name);

        if (info == null) {
            throw new IllegalArgumentException("Addin does not exist: " + name);
        }

        if (info.getAddinPackage() == null) {
            //如果未加载，则自动加载
            info.setAddinPackage(loadJar(info.getFile()));
        }

        if (info.getStarted()) {
            return;
        }

        info.getAddinPackage().start();
    }

    public static void stop(String name){
        PluginInfo info = pluginMap.get(name);

        if (info == null) {
            throw new IllegalArgumentException("Addin does not exist: " + name);
        }

        if(info.getStarted() == false){
            return;
        }

        if(info.getAddinPackage() != null){
            info.getAddinPackage().prestop();
            info.getAddinPackage().stop();
        }
    }


    /**
     * 加载 jar 插件包
     *
     * @param file 文件
     * */
    public static PluginPackage loadJar(File file) {
        try {
            URL url = file.toURI().toURL();
            PluginClassLoader classLoader = new PluginClassLoader(AppClassLoader.global());
            classLoader.addJar(url);

            List<PluginEntity> plugins = new ArrayList<>();

            PluginUtil.scanPlugins(classLoader, url.toString(), plugins::add);


            return new PluginPackage(file, classLoader, plugins);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 卸载 Jar 插件包
     *
     * @param pluginPackage 插件包
     * */
    public static void unloadJar(PluginPackage pluginPackage) {
        try {
            pluginPackage.prestop();
            pluginPackage.stop();

            PluginClassLoader classLoader = pluginPackage.getClassLoader();

            classLoader.removeJar(pluginPackage.getFile());
            classLoader.close();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
