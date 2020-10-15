package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;

/**
 * 插件实体
 *
 * @author noear
 * @since 1.0
 * */
public class XPluginEntity {
    /** 类名（全路径） */
    public String className;
    /*** 优先级（大的优先） */
    public int priority = 0;
    /** 插件 */
    public XPlugin plugin;

    public XPluginEntity(){}
    public XPluginEntity(XPlugin plugin){
        this.plugin = plugin;
    }


    /**
     * 优先级
     * */
    public int getPriority() {
        return priority;
    }

    /**
     * 启动
     * */
    public void start() {
        if (plugin == null) {
            plugin = XUtil.newInstance(className);
        }

        if (plugin != null) {
            plugin.start(XApp.global());
        }
    }

    /**
     * 停止
     * */
    public void stop(){
        if (plugin != null) {
            try {
                plugin.stop();
            } catch (Throwable ex) {
                //ex.printStackTrace();
            }
        }
    }
}
