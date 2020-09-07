package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.XContext;

public interface WhitelistChecker {
    boolean check(Whitelist anno, XContext ctx);
}
