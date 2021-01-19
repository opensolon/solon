package org.noear.solon.cloud;

/**
 * 默认云日志器（当没有服务时，给个空的；不致于出错）
 *
 * @author noear
 * @since 1.2
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
}
