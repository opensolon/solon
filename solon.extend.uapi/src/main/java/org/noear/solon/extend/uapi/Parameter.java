package org.noear.solon.extend.uapi;

import org.noear.solon.core.XContext;

public interface Parameter<P> {
    P build(XContext ctx);
}
