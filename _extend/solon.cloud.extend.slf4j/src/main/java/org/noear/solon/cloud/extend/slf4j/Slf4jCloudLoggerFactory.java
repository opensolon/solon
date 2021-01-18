package org.noear.solon.cloud.extend.slf4j;

import org.noear.solon.cloud.model.Level;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * @author noear 2021/1/18 created
 */
public enum Slf4jCloudLoggerFactory implements ILoggerFactory {
    /**
     * 工厂单例
     */
    INSTANCE;


    /**
     * 日志等级（INFO 内容太多了）
     */
    private volatile Level level = Level.WARN;

    /**
     * 书写器
     * */
    private volatile Slf4jCloudWriter writer = new Slf4jCloudWriterImp();

    Slf4jCloudLoggerFactory() {

    }

    @Override
    public Logger getLogger(String name) {
        return new Slf4jCloudLogger(name);
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return this.level;
    }

    public void setWriter(Slf4jCloudWriter writer) {
        this.writer = writer;
    }

    public void write(String name, Level level, String content) {
        if (writer == null) {
            return;
        }

        writer.write(name, level, content);
    }
}
