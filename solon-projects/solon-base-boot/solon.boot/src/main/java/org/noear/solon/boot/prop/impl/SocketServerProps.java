package org.noear.solon.boot.prop.impl;

import org.noear.solon.boot.ServerConstants;

/**
 * @author noear
 * @since 1.8
 */
public class SocketServerProps extends BaseServerProps {
    public SocketServerProps(int portBase) {
        super(ServerConstants.SIGNAL_SOCKET, portBase);
    }
}