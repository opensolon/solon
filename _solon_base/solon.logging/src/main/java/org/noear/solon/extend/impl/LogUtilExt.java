package org.noear.solon.extend.impl;

import org.noear.solon.Solon;
import org.noear.solon.core.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear
 * @since 1.10
 */
public class LogUtilExt extends LogUtil {
    static final Logger log = LoggerFactory.getLogger(Solon.class);

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
