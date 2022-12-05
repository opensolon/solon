package org.noear.solon.boot.prop.impl;

/**
 * @author noear
 * @since 1.8
 */
public class WebSocketServerProps extends BaseServerProps {

    public WebSocketServerProps(int portBase) {
        super("websocket", portBase);
    }
}