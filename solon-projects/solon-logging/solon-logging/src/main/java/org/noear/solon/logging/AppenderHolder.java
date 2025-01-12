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
package org.noear.solon.logging;

import org.noear.solon.Solon;
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
    private Appender real;
    private boolean printed;

    /**
     * 打印的
     * */
    protected boolean printed(){
        return printed;
    }

    public AppenderHolder(String name, Appender real, boolean printed) {
        this.real = real;
        this.name = name;
        this.printed = printed;

        //设置名称
        real.setName(name);
        //开始生命周期
        real.start();

        if (Solon.app() != null) {
            String levelStr = Solon.cfg().get("solon.logging.appender." + getName() + ".level");

            //设置级别
            setLevel(Level.of(levelStr, real.getDefaultLevel()));

            //是否启用
            enable = Solon.cfg().getBool("solon.logging.appender." + getName() + ".enable", true);

            Map<String, Object> meta = new LinkedHashMap();
            meta.put("level", getLevel().name());
            meta.put("enable", enable);
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
        if (enable == false) {
            return;
        }

        if (this.level.code > logEvent.getLevel().code) {
            return;
        }

        real.append(logEvent);
    }

    /**
     * 重置状态（等级与可用）
     * */
    public void reset(){
        if (Solon.app() != null) {
            String levelStr = Solon.cfg().get("solon.logging.appender." + getName() + ".level");

            //设置级别
            setLevel(Level.of(levelStr, real.getDefaultLevel()));

            //是否启用
            enable = Solon.cfg().getBool("solon.logging.appender." + getName() + ".enable", true);
        } else {
            setLevel(real.getDefaultLevel());
        }
    }

    /**
     * 停止生命周期
     * */
    public void stop(){
        real.stop();
    }
}
