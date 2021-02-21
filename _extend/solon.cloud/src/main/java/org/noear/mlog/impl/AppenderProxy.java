package org.noear.mlog.impl;

import org.noear.mlog.Appender;
import org.noear.mlog.AppenderSimple;
import org.noear.mlog.Level;
import org.noear.mlog.Metainfo;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author noear
 * @since 1.3
 */
public class AppenderProxy extends AppenderSimple {
    private static AppenderProxy instance;

    public static AppenderProxy getInstance() {
        if (instance == null) {
            instance = new AppenderProxy();
        }

        return instance;
    }

    protected Map<String,Appender> appenderMap = new LinkedHashMap<>();

    public void add(Appender appender) {
        appenderMap.putIfAbsent(appender.getName(), appender);
    }

    private AppenderProxy() {
        add(new AppenderConsoleImp());
    }


    @Override
    public String getName() {
        return "proxy";
    }

    @Override
    public void append(String loggerName, Class<?> clz, Level level, Metainfo metainfo, Object content) {
        for (Appender appender : appenderMap.values()) {
            appender.append(loggerName, clz, level, metainfo, content);
        }
    }
}
