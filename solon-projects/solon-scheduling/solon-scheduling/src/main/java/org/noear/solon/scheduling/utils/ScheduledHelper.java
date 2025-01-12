/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.scheduling.utils;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.scheduling.ScheduledAnno;

import java.util.Properties;

/**
 * 调整助手
 *
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
            Properties prop = Solon.cfg().getProp("solon.scheduling.job." + warpper.name());

            if (prop.size() > 0) {
                String enableStr = prop.getProperty("enable");

                String cronStr = prop.getProperty("cron");
                String zoneStr = prop.getProperty("zone");

                String fixedRateStr = prop.getProperty("fixedRate");
                String fixedDelayStr = prop.getProperty("fixedDelay");
                String initialDelayStr = prop.getProperty("initialDelay");

                if ("false".equals(enableStr)) {
                    warpper.enable(false);
                }

                if (Utils.isNotEmpty(cronStr)) {
                    if (cronStr.length() > 6 && cronStr.contains(" ")) {
                        warpper.cron(cronStr);
                    } else {
                        warpper.fixedRate(fixedRate(cronStr));
                    }
                }

                if(Utils.isNotEmpty(zoneStr)){
                    warpper.zone(zoneStr);
                }

                if (Utils.isNotEmpty(fixedRateStr)) {
                    warpper.fixedRate(Long.parseLong(fixedRateStr));
                }

                if (Utils.isNotEmpty(fixedDelayStr)) {
                    warpper.fixedDelay(Long.parseLong(fixedDelayStr));
                }

                if (Utils.isNotEmpty(initialDelayStr)) {
                    warpper.initialDelay(Long.parseLong(initialDelayStr));
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
