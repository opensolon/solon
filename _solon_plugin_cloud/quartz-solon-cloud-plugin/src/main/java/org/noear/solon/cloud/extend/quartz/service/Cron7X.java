package org.noear.solon.cloud.extend.quartz.service;

import org.noear.solon.Utils;

import java.time.ZoneId;

/**
 * @author noear
 * @since 2.0
 */
public class Cron7X {
    private String cron;
    private ZoneId zone;
    private Long interval;

    /**
     * cron 表达式
     */
    public String getCron() {
        return cron;
    }

    /**
     * 时区
     */
    public ZoneId getZone() {
        return zone;
    }

    /**
     * 毫秒数
     */
    public Long getInterval() {
        return interval;
    }

    public static Cron7X parse(String cron7x) throws IllegalArgumentException {
        if (Utils.isEmpty(cron7x)) {
            throw new IllegalArgumentException("The cron7x expression is empty");
        }

        Cron7X tmp = new Cron7X();

        if (cron7x.indexOf(" ") < 0) {
            if (cron7x.endsWith("ms")) {
                tmp.interval = Long.parseLong(cron7x.substring(0, cron7x.length() - 2));
            } else if (cron7x.endsWith("s")) {
                tmp.interval = Long.parseLong(cron7x.substring(0, cron7x.length() - 1)) * 1000;
            } else if (cron7x.endsWith("m")) {
                tmp.interval = Long.parseLong(cron7x.substring(0, cron7x.length() - 1)) * 1000 * 60;
            } else if (cron7x.endsWith("h")) {
                tmp.interval = Long.parseLong(cron7x.substring(0, cron7x.length() - 1)) * 1000 * 60 * 60;
            } else if (cron7x.endsWith("d")) {
                tmp.interval = Long.parseLong(cron7x.substring(0, cron7x.length() - 1)) * 1000 * 60 * 60 * 24;
            } else {
                throw new IllegalArgumentException("Unsupported cron7x expression: " + cron7x);
            }
        } else {
            int tzIdx = cron7x.indexOf("+");
            if (tzIdx < 0) {
                tzIdx = cron7x.indexOf("-");
            }

            if (tzIdx > 0) {
                String tz = cron7x.substring(tzIdx);
                tmp.zone = ZoneId.of(tz);
                tmp.cron = cron7x.substring(0, tzIdx - 1);
            } else {
                tmp.cron = cron7x;
            }
        }

        return tmp;
    }
}
