package org.noear.solon.boot.rapidoid_http_fast;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;

public final class XPluginImp implements XPlugin {


    public static String solon_boot_ver(){
        return "jlhttp 2.4/1.0.5.5";
    }

    @Override
    public  void start(XApp app) {
        if(app.enableHttp == false){
            return;
        }
    }

    @Override
    public void stop() throws Throwable {

    }
}
