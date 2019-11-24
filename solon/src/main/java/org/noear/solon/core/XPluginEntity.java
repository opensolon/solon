package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;

import java.io.Closeable;

public class XPluginEntity {
    public String className;//classFullName
    public int priority = 0;
    public XPlugin plugin;

    public XPluginEntity(){}
    public XPluginEntity(XPlugin plugin){
        this.plugin = plugin;
    }

    public void start() {
        if (plugin == null) {
            plugin = XUtil.newClass(className);
        }

        if (plugin != null) {
            plugin.start(XApp.global());
        }
    }

    public void stop(){
        if (plugin != null) {
            try {
                plugin.stop();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }
}
