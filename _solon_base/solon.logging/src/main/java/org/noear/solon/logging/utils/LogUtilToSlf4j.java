package org.noear.solon.logging.utils;

import org.noear.solon.Solon;
import org.noear.solon.core.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear
 * @since 1.10
 */
public class LogUtilToSlf4j extends LogUtil {
    static final Logger log = LoggerFactory.getLogger(Solon.class);

    @Override
    public void trace(String content) {
        log.trace("[Solon] " + content);
    }

    @Override
    public void debug(String content) {
        log.debug("[Solon] " + content);
    }

    @Override
    public void info(String content) {
        log.info("[Solon] " + content);
    }

    @Override
    public void warn(String content) {
        log.warn("[Solon] " + content);
    }

    @Override
    public void error(String content) {
        log.error("[Solon] " + content);
    }
}
