package org.beetl.sql.ext.solon.test.masterslave;

import org.noear.solon.XApp;

public class MasterSlaveApp {
    public static void main(String[] args) {
        XApp.start(MasterSlaveApp.class, args, (app) -> {
            app.prop().loadAdd("application-master-slave.properties");
        });
    }
}
