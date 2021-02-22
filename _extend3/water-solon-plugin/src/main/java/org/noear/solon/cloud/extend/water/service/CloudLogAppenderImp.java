package org.noear.solon.cloud.extend.water.service;

import org.noear.mlog.Level;
import org.noear.mlog.Metainfo;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.extend.water.WaterProps;
import org.noear.solon.cloud.impl.CloudLogAppenderSimple;
import org.noear.water.WaterClient;
import org.noear.water.dso.LogPipeline;
import org.noear.water.log.LogEvent;
import org.noear.water.utils.Datetime;
import org.noear.water.utils.TextUtils;

/**
 * @author noear
 * @since 1.3
 */
public class CloudLogAppenderImp extends CloudLogAppenderSimple {
    private String loggerNameDefault;

    public CloudLogAppenderImp() {
        super();

        loggerNameDefault = WaterProps.instance.getLogDefault();

        if (Utils.isEmpty(loggerNameDefault)) {
            if (Utils.isNotEmpty(Solon.cfg().appName())) {
                loggerNameDefault = Solon.cfg().appName() + "_log";
            }
        }

        if (Utils.isEmpty(loggerNameDefault)) {
            System.err.println("[WARN] Solon.cloud no default logger is configured");
        }
    }

    @Override
    public String getName() {
        return "water";
    }

    @Override
    protected void appendDo(String loggerName, Class<?> clz, Level level, Metainfo metainfo, Object content) {
        if (clz != null) {
            loggerName = loggerNameDefault;
        }

        if (Utils.isEmpty(loggerName)) {
            return;
        }

        Datetime datetime = Datetime.Now();

        LogEvent log = new LogEvent();

        log.logger = loggerName;
        log.level = (level.code / 10);
        log.content = content;

        if (metainfo != null) {
            log.tag = metainfo.get("tag0");
            log.tag1 = metainfo.get("tag1");
            log.tag2 = metainfo.get("tag2");
            log.tag3 = metainfo.get("tag3");
        }

        if (clz != null) {
            if (TextUtils.isEmpty(log.tag3)) {
                log.tag3 = clz.getSimpleName();
            }
        }

        log.trace_id = WaterClient.waterTraceId();
        log.from = WaterClient.localServiceHost();
        log.thread = Thread.currentThread().getName();

        log.log_date = datetime.getDate();
        log.log_fulltime = datetime.getFulltime();

        LogPipeline.singleton().add(log);
    }
}
