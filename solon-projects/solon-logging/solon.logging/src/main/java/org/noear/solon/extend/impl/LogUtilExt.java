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
     */
    private void incubate() {
        ServiceLoader<LogIncubator> internetServices = ServiceLoader.load(LogIncubator.class);
        for (LogIncubator logIncubator : internetServices) {
            try {
                logIncubator.incubate();
            } catch (Throwable e) {
                // native: 静态扩展，初始化 LogUtilExt 报错时，此处可以将异常打印出来
                e.printStackTrace();
                throw new IllegalStateException(e);
            }
            break;
        }
    }

    @Override
    public void trace(String content) {
        log.trace(title() + content);
    }

    @Override
    public void debug(String content) {
        log.debug(title() + content);
    }

    @Override
    public void info(String content) {
        log.info(title() + content);
    }

    @Override
    public void warn(String content, Throwable throwable) {
        if (throwable == null) {
            log.warn(title() + content);
        } else {
            log.warn(title() + content, throwable);
        }
    }

    @Override
    public void error(String content, Throwable throwable) {
        if (throwable == null) {
            log.error(title() + content);
        } else {
            log.error(title() + content, throwable);
        }
    }
}
