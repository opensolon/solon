package org.noear.solon.core.util;

import org.noear.solon.Utils;
import org.noear.solon.core.PluginEntity;

import java.net.URL;
import java.util.Collection;
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
     *
     * @param classLoader 类加载器
     * @param limitFile   限制文件
     */
    public static void scanPlugins(ClassLoader classLoader, String limitFile, Consumer<PluginEntity> consumer) {
        //3.查找插件配置（如果出错，让它抛出异常）
        Collection<String> nameList = ScanUtil.scan(classLoader, "META-INF/solon", n -> n.endsWith(".properties"));
        boolean isLimitFile = Utils.isNotEmpty(limitFile);

        for (String name : nameList) {
            URL res = ResourceUtil.getResource(classLoader, name);

            if (res == null) {
                // native 时，扫描出来的resource可能是不存在的（这种情况就是bug），需要给于用户提示，反馈给社区
                LogUtil.global().warn("Solon plugin: name=" + name + ", resource is null");
            } else {
                if (isLimitFile && name.indexOf(limitFile) < 0) {
                    continue;
                }

                Properties props = Utils.loadProperties(res);
                findPlugins(classLoader, props, consumer);
            }
        }
    }

    /**
     * 查找插件
     */
    public static void findPlugins(ClassLoader classLoader, Properties props, Consumer<PluginEntity> consumer) {
        String pluginStr = props.getProperty("solon.plugin");

        if (Utils.isNotEmpty(pluginStr)) {
            String priorityStr = props.getProperty("solon.plugin.priority");
            int priority = 0;
            if (Utils.isNotEmpty(priorityStr)) {
                priority = Integer.parseInt(priorityStr);
            }

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