package org.noear.solon.cloud.extend.water.service;

import org.noear.mlog.Level;
import org.noear.mlog.LoggerSimple;
import org.noear.mlog.Metainfo;
import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudLogger;
import org.noear.solon.cloud.extend.water.WaterProps;
import org.noear.water.WaterClient;
import org.noear.water.dso.LogPipeline;
import org.noear.water.log.LogEvent;
import org.noear.water.utils.Datetime;
import org.noear.water.utils.TextUtils;

import java.util.Date;

/**
 * @author noear
 * @since 1.2
 */
public class CloudLoggerImp extends LoggerSimple implements  CloudLogger {

    public CloudLoggerImp(String name) {
        super(name);
    }

    public CloudLoggerImp(Class<?> clz) {
        super(clz);
        this.name = WaterProps.instance.getLogDefault();

        if (Utils.isEmpty(name)) {
            System.err.println("[WARN] Solon.cloud no default logger is configured");
        }
    }

    @Override
    public void write(Level level, Metainfo metainfo, Object content) {
        StringBuilder summary = new StringBuilder();
        summary.append("[").append(Thread.currentThread().getName()).append("]");

        if (clz != null) {
            if (metainfo == null) {
                metainfo = new Metainfo();
            }

            metainfo.put("tag3", clz.getSimpleName());
            summary.append("[").append(clz.getTypeName()).append("]");
        }

        if (TextUtils.isEmpty(getName())) {
            print0(level, metainfo, content);
        } else {
            if (metainfo == null) {
                write0(level, null, null, null, null, summary.toString(), content);
            } else {
                write0(level,
                        metainfo.get("tag0"),
                        metainfo.get("tag1"),
                        metainfo.get("tag2"),
                        metainfo.get("tag3"),
                        summary.toString(), content);
            }
        }
    }

    private void write0(Level level, String tag, String tag1, String tag2, String tag3, String summary, Object content) {
        if (TextUtils.isEmpty(getName())) {
            return;
        }

        Datetime datetime = Datetime.Now();

        LogEvent log = new LogEvent();

        log.logger = getName();
        log.level = (level.code / 10);
        log.tag = tag;
        log.tag1 = tag1;
        log.tag2 = tag2;
        log.tag3 = tag3;
        log.summary = summary;
        log.content = content;

        if (clz != null) {
            if (TextUtils.isEmpty(summary)) {
                log.summary = clz.getTypeName();
            }

            if (TextUtils.isEmpty(tag3)) {
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

    private void print0(Level level, Metainfo metainfo, Object content) {
        StringBuilder buf = new StringBuilder();
        buf.append("[").append(level.name()).append("]");
        buf.append(new Date());
        buf.append("[").append(Thread.currentThread().getName()).append("]");
        if (clz != null) {
            buf.append("[").append(clz.getTypeName()).append("]");
        }

        if (metainfo != null) {
            buf.append(metainfo.toString());
        }

        buf.append("::");

        if (content instanceof String) {
            buf.append(content);
        } else {
            buf.append(ONode.loadObj(content));
        }

        if (level == Level.ERROR) {
            System.err.println(buf.toString());
        } else {
            System.out.println(buf.toString());
        }
    }
}
