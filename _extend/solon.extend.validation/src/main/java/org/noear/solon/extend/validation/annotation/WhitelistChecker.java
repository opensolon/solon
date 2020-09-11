package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.XContext;

/**
 *
 * @author noear
 * @since 1.0.24
 * */
public interface WhitelistChecker {
    boolean check(Whitelist anno, XContext ctx);
}
