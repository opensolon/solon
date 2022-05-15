package org.noear.solon.core.util;

import org.noear.solon.Utils;
import org.noear.solon.core.PluginEntity;
import org.noear.solon.core.Props;

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
                    if (Utils.isNotEmpty(limitFile)) {
                        if (n.contains(limitFile) == false) {
                            return false;
                        }
                    }

                    return n.endsWith(".properties") || n.endsWith(".yml");
                })
                .stream()
                .map(k -> Utils.getResource(classLoader, k))
                .forEach(url -> {
                    Props props = new Props(Utils.loadProperties(url));
                    findPlugins(classLoader, props, consumer);
                });


    }

    /**
     * 查找插件
     */
    public static void findPlugins(ClassLoader classLoader, Props props, Consumer<PluginEntity> consumer) {
        String pluginStr = props.get("solon.plugin");

        if (Utils.isNotEmpty(pluginStr)) {
            int priority = props.getInt("solon.plugin.priority", 0);
            String[] plugins = pluginStr.trim().split(",");

            for (String clzName : plugins) {
                if (clzName.length() > 0) {
                    PluginEntity ent = new PluginEntity(classLoader, clzName.trim());
                    ent.setPriority(priority);
                    consumer.accept(ent);
                }
            }
        }
    }
}