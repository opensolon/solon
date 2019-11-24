package org.noear.solon.extend.stop;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        boolean enabled = app.prop().getBool("solon.stop.enabled",false);
        String path = app.prop().get("solon.stop.path","/_run/_stop/");
        String host = app.prop().get("solon.stop.host","127.0.0.1");

        if(enabled){
            app.get(path, (c)->{
                if("*".equals(host)){
                    XApp.stop();
                }else if(host.equals(c.uri().getHost())){
                    XApp.stop();
                }
            });
        }

    }
}
