package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;

public class XPluginEntity {
    public String plugin;//classFullName
    public int priority = 0;

    public void load(){
        XPlugin p1 = XUtil.newClass(plugin);
        if (p1 != null) {
            XApp.global().plug(p1);
        }
    }
}
