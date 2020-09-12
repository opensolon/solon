package org.beetl.sql.ext.solon.test.simple;

import org.noear.solon.XApp;

public class SimpleApp {
    public static void main(String[] args) {
        XApp.start(SimpleApp.class, args, (app) -> {
            app.prop().loadAdd("application-simple.properties");
        });
    }
}
