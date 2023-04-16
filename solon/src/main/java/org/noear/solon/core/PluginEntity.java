package org.noear.solon.core;

import org.noear.solon.SolonProps;
import org.noear.solon.core.bean.InitializingBean;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.lang.Nullable;

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

    /**
     * 插件属性（申明插件类与优先级）
     */
    private Properties props;

    public PluginEntity(ClassLoader classLoader, String className, Properties props) {
        this.classLoader = classLoader;
        this.className = className;
        this.props = props;
    }

    /**
     * @deprecated 2.2
     */
    @Deprecated
    public PluginEntity(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * @deprecated 2.2
     */
    @Deprecated
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
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * 获取插件
     */
    public @Nullable Plugin getPlugin() {
        return plugin;
    }

    public Properties getProps() {
        return props;
    }

    public String getClassName() {
        return className;
    }

    /**
     * 初始化
     */
    public void init(AopContext context) {
        initInstance(context);
    }

    /**
     * 启动
     */
    public void start(AopContext context) {
        if (plugin != null) {
            try {
                plugin.start(context);
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                throw new IllegalStateException("Plugin start failed", e);
            }
        }
    }

    /**
     * 预停止
     */
    public void prestop() {
        if (plugin != null) {
            try {
                plugin.prestop();
            } catch (Throwable e) {
                //e.printStackTrace();
            }
        }
    }

    /**
     * 停止
     */
    public void stop() {
        if (plugin != null) {
            try {
                plugin.stop();
            } catch (Throwable e) {
                //e.printStackTrace();
            }
        }
    }

    /**
     * 初始化
     */
    private void initInstance(AopContext context) {
        if (plugin == null) {
            if (classLoader != null) {
                plugin = ClassUtil.newInstance(classLoader, className);

                if (plugin == null) {
                    LogUtil.global().warn("The configured plugin cannot load: " + className);
                } else {
                    if (plugin instanceof InitializingBean) {
                        try {
                            ((InitializingBean) plugin).afterInjection();
                        } catch (RuntimeException e) {
                            throw e;
                        } catch (Throwable e) {
                            throw new IllegalStateException(e);
                        }
                    }
                }
            }
        }
    }
}
