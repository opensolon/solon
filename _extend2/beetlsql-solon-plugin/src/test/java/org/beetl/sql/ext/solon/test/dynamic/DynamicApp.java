package org.beetl.sql.ext.solon.test.dynamic;

import org.noear.solon.XApp;

public class DynamicApp {
    public static void main(String[] args) {
        XApp.start(DynamicApp.class, args, (app) -> {
            app.prop().loadAdd("application-dynamic.properties");
        });
    }
}
