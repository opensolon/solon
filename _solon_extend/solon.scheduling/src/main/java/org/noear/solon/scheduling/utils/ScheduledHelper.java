package org.noear.solon.scheduling.utils;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.scheduling.ScheduledAnno;

import java.util.Properties;

/**
 * @author noear
 * @since 1.11
 */
public class ScheduledHelper {
    public static void resetScheduled(ScheduledAnno warpper) {
        if (warpper.cron().length() < 6 || warpper.cron().indexOf(" ") < 0) {
            warpper.fixedRate(fixedRate(warpper.cron()));
            warpper.cron("");
        } else {
            warpper.fixedRate(0L);
        }
    }

    /**
     * 配置加持
     */
    public static void configScheduled(ScheduledAnno warpper) {
        if (warpper.cron().length() < 6 || warpper.cron().indexOf(" ") < 0) {
            if (warpper.fixedRate() == 0) {
                warpper.fixedRate(fixedRate(warpper.cron()));
            }

            warpper.cron("");
        }

        if (Utils.isNotEmpty(warpper.name())) {
            Properties prop = Solon.cfg().getProp("solon.scheduling." + warpper.name());

            if (prop.size() > 0) {
                String cronTmp = prop.getProperty("cron");
                String enableTmp = prop.getProperty("enable");

                if ("false".equals(enableTmp)) {
                    warpper.enable(false);
                }

                if (Utils.isNotEmpty(cronTmp)) {
                    if (cronTmp.length() > 6 && cronTmp.contains(" ")) {
                        warpper.cron(cronTmp);
                    } else {
                        warpper.fixedRate(fixedRate(cronTmp));
                    }

                }
            }
        }
    }

    public static long fixedRate(String cronx) {
        if (cronx.endsWith("ms")) {
            long period = Long.parseLong(cronx.substring(0, cronx.length() - 2));
            return period;
        } else if (cronx.endsWith("s")) {
            long period = Long.parseLong(cronx.substring(0, cronx.length() - 1));
            return period * 1000;
        } else if (cronx.endsWith("m")) {
            long period = Long.parseLong(cronx.substring(0, cronx.length() - 1));
            return period * 1000 * 60;
        } else if (cronx.endsWith("h")) {
            long period = Long.parseLong(cronx.substring(0, cronx.length() - 1));
            return period * 1000 * 60 * 60;
        } else if (cronx.endsWith("d")) {
            long period = Long.parseLong(cronx.substring(0, cronx.length() - 1));
            return period * 1000 * 60 * 60 * 24;
        } else {
            return 0L;
        }
    }
}
