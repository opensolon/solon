package org.noear.solon.addin;

import org.noear.solon.Solon;
import org.noear.solon.core.JarClassLoader;
import org.noear.solon.core.PluginEntity;
import org.noear.solon.core.util.PluginUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author noear
 * @since 1.7
 */
public class AddinManager {
    static Map<String, AddinInfo> addinInfoMap = new HashMap<>();

    static {
        Properties pops = Solon.cfg().getProp("solon.addin");
        if (pops.size() > 0) {
            pops.forEach((k, v) -> {
                if (k instanceof String && v instanceof String) {
                    add((String) k, new File((String) v));
                }
            });
        }
    }

    public Collection<AddinInfo> getAll(){
        return addinInfoMap.values();
    }

    public static void add(String name, File file){
        if(addinInfoMap.containsKey(name)){
            return;
        }

        addinInfoMap.put(name, new AddinInfo(name, file));
    }

    public static void remove(String name){
        addinInfoMap.remove(name);
    }


    public static AddinPackage load(String name) {
        AddinInfo info = addinInfoMap.get(name);

        if (info == null) {
            throw new IllegalArgumentException("Addin does not exist: " + name);
        }

        if (info.getAddinPackage() == null) {
            info.setAddinPackage(loadJar(info.getFile()));
        }

        return info.getAddinPackage();
    }

    public static void unload(String name){
        AddinInfo info = addinInfoMap.get(name);

        if (info == null) {
            throw new IllegalArgumentException("Addin does not exist: " + name);
        }

        unloadJar(info.getAddinPackage());
    }

    public static void start(String name){
        AddinInfo info = addinInfoMap.get(name);

        if (info == null) {
            throw new IllegalArgumentException("Addin does not exist: " + name);
        }

        if(info.getAddinPackage() != null){
            info.getAddinPackage().start();
        }
    }

    public static void stop(String name){
        AddinInfo info = addinInfoMap.get(name);

        if (info == null) {
            throw new IllegalArgumentException("Addin does not exist: " + name);
        }

        if(info.getAddinPackage() != null){
            info.getAddinPackage().prestop();
            info.getAddinPackage().stop();
        }
    }


    /**
     * 加载jar插件包
     * */
    public static AddinPackage loadJar(File file) {
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
    public static void unloadJar(AddinPackage pluginPackage) {
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
