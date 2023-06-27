package org.noear.solon.boot.prop.impl;

import org.noear.solon.boot.ServerConstants;

/**
 * @author noear
 * @since 1.8
 */
public class HttpServerProps extends BaseServerProps {
    public HttpServerProps() {
        super(ServerConstants.SIGNAL_HTTP, 0);
    }
}
