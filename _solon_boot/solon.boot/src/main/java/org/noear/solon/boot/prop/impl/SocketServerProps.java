package org.noear.solon.boot.prop.impl;

/**
 * @author noear
 * @since 1.8
 */
public class SocketServerProps extends BaseServerProps {
    public SocketServerProps(int portBase) {
        super("socket", portBase);
    }
}