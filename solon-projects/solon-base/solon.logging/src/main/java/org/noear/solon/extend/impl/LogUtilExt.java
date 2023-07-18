package org.noear.solon.extend.impl;

import org.noear.solon.Solon;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.logging.LogIncubator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

/**
 * 把内核日志转到 Slf4j 接口
 *
 * @author noear
 * @since 2.3
 */
public class LogUtilExt extends LogUtil {
    static final Logger log = LoggerFactory.getLogger(Solon.class);

    public LogUtilExt() {
        if (Solon.app() != null) {
            incubate();
        }
    }

    /**
     * 孵化日志实现（加载配置，转换格式）
     * */
    private void incubate(){
        ServiceLoader<LogIncubator> internetServices = ServiceLoader.load(LogIncubator.class);
        for (LogIncubator logIncubator : internetServices) {
            try {
                logIncubator.incubate();
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
            break;
        }
    }

    @Override
    public void trace(String content) {
        log.trace(content);
    }

    @Override
    public void debug(String content) {
        log.debug(content);
    }

    @Override
    public void info(String content) {
        log.info(content);
    }

    @Override
    public void warn(String content) {
        log.warn(content);
    }

    @Override
    public void error(String content) {
        log.error(content);
    }
}
