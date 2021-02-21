package org.noear.mlog.impl;

import org.noear.mlog.AppenderSimple;
import org.noear.mlog.Level;
import org.noear.mlog.Metainfo;
import org.noear.solon.Solon;
import org.noear.water.WaterClient;
import org.noear.water.dso.LogPipeline;
import org.noear.water.log.LogEvent;
import org.noear.water.utils.Datetime;
import org.noear.water.utils.TextUtils;

/**
 * @author noear
 * @since 1.3
 */
public class AppenderWaterImp extends AppenderSimple {
    public AppenderWaterImp() {
        String levelStr = Solon.cfg().get("solon.mlog.appender." + getName() + ".level");
        setLevel(Level.of(levelStr, Level.INFO));
    }

    @Override
    public String getName() {
        return "water";
    }

    @Override
    protected void appendDo(String name, Class<?> clz, Level level, Metainfo metainfo, Object content) {
        if (TextUtils.isEmpty(getName())) {
            return;
        }

        Datetime datetime = Datetime.Now();

        LogEvent log = new LogEvent();

        log.logger = getName();
        log.level = (level.code / 10);
        log.tag = metainfo.get("tag0");
        log.tag1 = metainfo.get("tag1");
        log.tag2 = metainfo.get("tag2");
        log.tag3 = metainfo.get("tag3");
        log.content = content;

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
