package org.noear.solon.core;

import org.noear.solon.Solon;
import org.noear.solon.SolonProps;
import org.noear.solon.Utils;

import java.util.Properties;

/**
 * 插件实体
 *
 * @see SolonProps#plugsScan()
 * @author noear
 * @since 1.0
 * */
public class PluginEntity {
    /**
     * 类名（全路径）
     */
    private String className;
    /**
     * 类加载器
     */
    private ClassLoader classLoader;
    /**
     * 优先级（大的优先）
     */
    private int priority = 0;
    /**
     * 插件
     */
    private Plugin plugin;

    private Properties props;

    public PluginEntity(ClassLoader classLoader, String className, Properties props) {
        this.classLoader = classLoader;
        this.className = className;
        this.props = props;
    }

    public PluginEntity(Plugin plugin) {
        this.plugin = plugin;
    }

    public PluginEntity(Plugin plugin, int priority) {
        this.plugin = plugin;
        this.priority = priority;
    }


    /**
     * 获取优先级
     */
    public int getPriority() {
        return priority;
    }

    /**
     * 设置优先级
     * */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * 获取插件
     */
    public Plugin getPlugin() {
        init();

        return plugin;
    }

    public Properties getProps() {
        return props;
    }

    /**
     * 启动
     */
    public void start(AopContext context) {
        init();

        if (plugin != null) {
            plugin.start(context);
        }
    }

    /**
     * 预停止
     */
    public void prestop() {
        init();

        if (plugin != null) {
            try {
                plugin.prestop();
            } catch (Throwable ex) {
                //ex.printStackTrace();
            }
        }
    }

    /**
     * 停止
     */
    public void stop() {
        init();

        if (plugin != null) {
            try {
                plugin.stop();
            } catch (Throwable ex) {
                //ex.printStackTrace();
            }
        }
    }

    /**
     * 初始化
     */
    private void init() {
        if (plugin == null) {
            if (classLoader != null) {
                plugin = Utils.newInstance(classLoader, className);
            }
        }
    }
}
