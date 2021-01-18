package org.noear.solon.cloud;

import org.noear.solon.cloud.model.Level;

/**
 * @author noear 2021/1/18 created
 */
public class CloudLoggerDefault implements CloudLogger{
    public static final CloudLogger instance = new CloudLoggerDefault();


    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public void trace(Object content) {

    }

    @Override
    public void trace(String summary, Object content) {

    }

    @Override
    public void trace(String tag1, String summary, Object content) {

    }

    @Override
    public void trace(String tag1, String tag2, String summary, Object content) {

    }

    @Override
    public void trace(String tag1, String tag2, String tag3, String summary, Object content) {

    }

    @Override
    public void trace(String tag1, String tag2, String tag3, String tag4, String summary, Object content) {

    }

    @Override
    public void debug(Object content) {

    }

    @Override
    public void debug(String summary, Object content) {

    }

    @Override
    public void debug(String tag1, String summary, Object content) {

    }

    @Override
    public void debug(String tag1, String tag2, String summary, Object content) {

    }

    @Override
    public void debug(String tag1, String tag2, String tag3, String summary, Object content) {

    }

    @Override
    public void debug(String tag1, String tag2, String tag3, String tag4, String summary, Object content) {

    }

    @Override
    public void info(Object content) {

    }

    @Override
    public void info(String summary, Object content) {

    }

    @Override
    public void info(String tag1, String summary, Object content) {

    }

    @Override
    public void info(String tag1, String tag2, String summary, Object content) {

    }

    @Override
    public void info(String tag1, String tag2, String tag3, String summary, Object content) {

    }

    @Override
    public void info(String tag1, String tag2, String tag3, String tag4, String summary, Object content) {

    }

    @Override
    public void warn(Object content) {

    }

    @Override
    public void warn(String summary, Object content) {

    }

    @Override
    public void warn(String tag1, String summary, Object content) {

    }

    @Override
    public void warn(String tag1, String tag2, String summary, Object content) {

    }

    @Override
    public void warn(String tag1, String tag2, String tag3, String summary, Object content) {

    }

    @Override
    public void warn(String tag1, String tag2, String tag3, String tag4, String summary, Object content) {

    }

    @Override
    public void error(Object content) {

    }

    @Override
    public void error(String summary, Object content) {

    }

    @Override
    public void error(String tag1, String summary, Object content) {

    }

    @Override
    public void error(String tag1, String tag2, String summary, Object content) {

    }

    @Override
    public void error(String tag1, String tag2, String tag3, String summary, Object content) {

    }

    @Override
    public void error(String tag1, String tag2, String tag3, String tag4, String summary, Object content) {

    }

    @Override
    public void write(Level level, String tag1, Object content) {

    }
}
