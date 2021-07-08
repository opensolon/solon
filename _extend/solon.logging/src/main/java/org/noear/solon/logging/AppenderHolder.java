package org.noear.solon.logging;

import org.noear.solon.Solon;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.logging.event.Appender;
import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.event.LogEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 添加器持有者（用于支持配置）
 *
 * @author noear
 * @since 1.3
 */
public final class AppenderHolder {
    Appender real;

    public AppenderHolder(String name, Appender real) {
        this.real = real;
        this.name = name;

        //设置名称
        real.setName(name);
        //开始生命周期
        real.start();

        if (Solon.global() != null) {
            String levelStr = Solon.cfg().get("solon.logging.appender." + getName() + ".level");

            //设置级别
            setLevel(Level.of(levelStr, real.getDefaultLevel()));

            //是否启用
            enable = Solon.cfg().getBool("solon.logging.appender." + getName() + ".enable", true);

            Map<String, Object> meta = new LinkedHashMap();
            meta.put("level", getLevel().name());
            meta.put("enable", enable);

            //打印无信息
            PrintUtil.info("Logging", getName() + " " + meta.toString());
        } else {
            setLevel(real.getDefaultLevel());
        }
    }

    private String name;

    /**
     * 获取名称
     * */
    public String getName() {
        return name;
    }

    private boolean enable = true;

    /**
     * 获取启用状态
     * */
    public boolean getEnable() {
        return enable;
    }

    private Level level;

    /**
     * 获取级别
     * */
    public Level getLevel() {
        return level;
    }

    /**
     * 设置级别
     * */
    public void setLevel(Level level) {
        this.level = level;
    }

    /**
     * 添加日志
     * */
    public void append(LogEvent logEvent) {
        if (enable == false || this.level.code > logEvent.getLevel().code) {
            return;
        }

        real.append(logEvent);
    }
}
