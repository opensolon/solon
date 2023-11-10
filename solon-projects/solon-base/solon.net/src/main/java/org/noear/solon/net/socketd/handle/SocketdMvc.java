package org.noear.solon.net.socketd.handle;

/**
 * Socket.D Mvc 工具类
 *
 * @author noear
 * @since 2.0
 */
public class SocketdMvc {
    private static final SocketdListenerToMvc listener = new SocketdListenerToMvc();

    /**
     * 获取转换监听
     * */
    public static SocketdListenerToMvc getListener() {
        return listener;
    }
}
