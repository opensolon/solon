package org.noear.mlog.impl;

import org.noear.mlog.LoggerSimple;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudLogger;
import org.noear.solon.cloud.extend.water.WaterProps;

/**
 * @author noear
 * @since 1.2
 */
public class LoggerWaterImp extends LoggerSimple implements  CloudLogger {

    public LoggerWaterImp(String name) {
        super(name);
    }

    public LoggerWaterImp(Class<?> clz) {
        super(clz);
        name = WaterProps.instance.getLogDefault();

        if (Utils.isEmpty(name)) {
            if (Utils.isNotEmpty(Solon.cfg().appName())) {
                name = Solon.cfg().appName() + "_log";
            }
        }

        if (Utils.isEmpty(name)) {
            System.err.println("[WARN] Solon.cloud no default logger is configured");
        }
    }
}
