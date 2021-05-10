package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.extend.water.WaterProps;
import org.noear.solon.cloud.service.CloudLogService;
import org.noear.solon.logging.event.LogEvent;
import org.noear.water.WaterClient;
import org.noear.water.dso.LogPipeline;
import org.noear.water.utils.Datetime;
import org.noear.water.utils.LogHelper;
import org.noear.water.utils.TextUtils;

/**
 * @author noear 2021/2/23 created
 */
public class CloudLogServiceWaterImp implements CloudLogService {
    private String loggerNameDefault;

    public CloudLogServiceWaterImp() {
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
    public void append(LogEvent logEvent) {
        String loggerName = logEvent.getLoggerName();
        if (logEvent.getInitClass() != null) {
            loggerName = loggerNameDefault;
        }

        if (Utils.isEmpty(loggerName)) {
            return;
        }

        if (loggerName.contains(".")) {
            loggerName = loggerNameDefault;
        }

        Datetime datetime = Datetime.Now();

        org.noear.water.log.LogEvent log = new org.noear.water.log.LogEvent();

        log.group = Solon.cfg().appGroup();
        log.logger = loggerName;
        log.level = (logEvent.getLevel().code / 10);
        log.content = LogHelper.contentAsString(logEvent.getContent());

        if (logEvent.getMetainfo() != null) {
            log.tag = logEvent.getMetainfo().get("tag0");
            log.tag1 = logEvent.getMetainfo().get("tag1");
            log.tag2 = logEvent.getMetainfo().get("tag2");
            log.tag3 = logEvent.getMetainfo().get("tag3");
        }

        if (logEvent.getInitClass() != null) {
            if (TextUtils.isEmpty(log.tag3)) {
                log.tag3 = logEvent.getInitClass().getSimpleName();
            }
            log.class_name = logEvent.getInitClass().getCanonicalName();
        }else{
            if(logEvent.getLoggerName().contains(".")){
                log.class_name = logEvent.getLoggerName();
            }
        }

        log.trace_id = WaterClient.waterTraceId();
        log.from = WaterClient.localServiceHost();
        log.thread_name = Thread.currentThread().getName();

        log.log_date = datetime.getDate();
        log.log_fulltime = datetime.getFulltime();

        LogPipeline.singleton().add(log);
    }
}
