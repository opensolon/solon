package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;

import java.io.Closeable;

public class XPluginEntity {
    public String className;//classFullName
    public int priority = 0;
    public XPlugin plugin;

    public void start(){
        plugin = XUtil.newClass(className);
        if (plugin != null) {
            XApp.global().plug(plugin);
        }
    }

    public void stop(){
        if (plugin != null) {
            if(plugin instanceof Closeable){
                try{
                    ((Closeable) plugin).close();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }
}
