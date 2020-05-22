package org.noear.solon.extend.uapi;

import org.noear.solon.core.XContext;

public interface ParamParser<P> {
    P run(XContext ctx);
}
