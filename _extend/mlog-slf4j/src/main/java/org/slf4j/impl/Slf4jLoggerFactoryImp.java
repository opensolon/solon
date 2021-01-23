package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.event.Level;

/**
 * @author noear
 * @since 1.2
 */
public enum Slf4jLoggerFactoryImp implements ILoggerFactory {
    /**
     * 工厂单例
     */
    INSTANCE;


    /**
     * 日志等级（INFO 内容太多了）
     */
    private volatile Level level = Level.WARN;


    Slf4jLoggerFactoryImp() {

    }

    @Override
    public Logger getLogger(String name) {
        return new Slf4jLoggerImp(name);
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return this.level;
    }
}
