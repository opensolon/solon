package org.noear.solon.core.util;

import org.noear.solon.Utils;
import org.noear.solon.core.PluginEntity;

import java.util.Properties;
import java.util.function.Consumer;

/**
 * @author noear
 * @since 1.7
 */
public class PluginUtil {

    /**
     * 扫描插件
     */
    public static void scanPlugins(ClassLoader classLoader, String limitFile, Consumer<PluginEntity> consumer) {
        //3.查找插件配置（如果出错，让它抛出异常）
        ScanUtil.scan(classLoader, "META-INF/solon", n -> {
                    return n.endsWith(".properties") || n.endsWith(".yml");
                })
                .stream()
                .map(k -> Utils.getResource(classLoader, k))
                .filter(url -> {
                    if (Utils.isNotEmpty(limitFile)) {
                        if (url.toString().contains(limitFile) == false) {
                            return false;
                        }
                    }

                    return true;
                })
                .forEach(url -> {
                    Properties props = Utils.loadProperties(url);
                    findPlugins(classLoader, props, consumer);
                });
    }

    /**
     * 查找插件
     */
    public static void findPlugins(ClassLoader classLoader, Properties props, Consumer<PluginEntity> consumer) {
        String pluginStr = props.getProperty("solon.plugin");

        if (Utils.isNotEmpty(pluginStr)) {
            int priority = Integer.parseInt(props.getProperty("solon.plugin.priority", "0"));
            String[] plugins = pluginStr.trim().split(",");

            for (String clzName : plugins) {
                if (clzName.length() > 0) {
                    PluginEntity ent = new PluginEntity(classLoader, clzName.trim(), props);
                    ent.setPriority(priority);
                    consumer.accept(ent);
                }
            }
        }
    }
}