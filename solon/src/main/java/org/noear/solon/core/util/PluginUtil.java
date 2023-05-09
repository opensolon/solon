package org.noear.solon.core.util;

import org.noear.solon.Utils;
import org.noear.solon.core.PluginEntity;

import java.net.URL;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * 插件工具
 *
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
                .map(k -> {
                    URL resource = ResourceUtil.getResource(classLoader, k);
                    if (resource == null) {
                        // native 时，扫描出来的resource可能是不存在的（这种情况就是bug），需要给于用户提示，反馈给社区
                        LogUtil.global().warn("solon plugin: name=" + k + ", resource is null");
                    }
                    return resource;
                })
                .filter(url -> {
                    if (url == null) {
                        return false;
                    } else {
                        if (Utils.isNotEmpty(limitFile)) {
                            return url.toString().contains(limitFile);
                        }
                        return true;
                    }
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