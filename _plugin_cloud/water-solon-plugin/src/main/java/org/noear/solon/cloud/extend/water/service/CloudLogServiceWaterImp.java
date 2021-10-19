package org.noear.solon.cloud.extend.water.service;

import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.extend.water.WaterProps;
import org.noear.solon.cloud.service.CloudLogService;
import org.noear.solon.logging.event.LogEvent;
import org.noear.water.WaterClient;
import org.noear.water.dso.LogPipeline;
import org.noear.water.utils.Datetime;

/**
 * 分布式日志服务
 *
 * @author noear
 * @since 1.3
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
        log.content = contentAsString(logEvent.getContent());

        if (logEvent.getMetainfo() != null) {
            log.tag = logEvent.getMetainfo().get("tag0");
            log.tag1 = logEvent.getMetainfo().get("tag1");
            log.tag2 = logEvent.getMetainfo().get("tag2");
            log.tag3 = logEvent.getMetainfo().get("tag3");
        }

        if (logEvent.getLoggerName().contains(".")) {
            log.class_name = logEvent.getLoggerName();
        }

        log.trace_id = WaterClient.waterTraceId();
        log.from = WaterClient.localServiceHost();
        log.thread_name = Thread.currentThread().getName();

        log.log_date = datetime.getDate();
        log.log_fulltime = datetime.getFulltime();

        LogPipeline.singleton().add(log);
    }

    private static String contentAsString(Object content) {
        if (content != null) {
            if (content instanceof String) {
                //处理字符串
                return (String) content;
            } else if (content instanceof Throwable) {
                //处理异常
                return Utils.throwableToString((Throwable) content);
            } else {
                //处理其它对象（进行json）
                return ONode.load(content).toJson();
            }
        }

        return null;
    }
}
