package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;

public class XPluginEntity {
    public String className;//classFullName
    public int priority = 0;
    public XPlugin plugin;

    public void load(){
        plugin = XUtil.newClass(className);
        if (plugin != null) {
            XApp.global().plug(plugin);
        }
    }
}
