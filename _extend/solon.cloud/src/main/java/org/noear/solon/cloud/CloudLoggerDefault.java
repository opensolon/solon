package org.noear.solon.cloud;

import org.noear.solon.cloud.model.log.Meta;

/**
 * 默认云日志器（当没有服务时，给个空的；不致于出错）
 *
 * @author noear
 * @since 1.2
 */
public class CloudLoggerDefault implements CloudLogger{
    public static final CloudLogger instance = new CloudLoggerDefault();

    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void trace(Object content) {

    }

    @Override
    public void trace(String format, Object[] args) {

    }

    @Override
    public void trace(Meta meta, Object content) {

    }

    @Override
    public void trace(Meta meta, String format, Object[] args) {

    }

    @Override
    public void debug(Object content) {

    }

    @Override
    public void debug(String format, Object[] args) {

    }

    @Override
    public void debug(Meta meta, Object content) {

    }

    @Override
    public void debug(Meta meta, String format, Object[] args) {

    }

    @Override
    public void info(Object content) {

    }

    @Override
    public void info(String format, Object[] args) {

    }

    @Override
    public void info(Meta meta, Object content) {

    }

    @Override
    public void info(Meta meta, String format, Object[] args) {

    }

    @Override
    public void warn(Object content) {

    }

    @Override
    public void warn(String format, Object[] args) {

    }

    @Override
    public void warn(Meta meta, Object content) {

    }

    @Override
    public void warn(Meta meta, String format, Object[] args) {

    }

    @Override
    public void error(Object content) {

    }

    @Override
    public void error(String format, Object[] args) {

    }

    @Override
    public void error(Meta meta, Object content) {

    }

    @Override
    public void error(Meta meta, String format, Object[] args) {

    }


}
