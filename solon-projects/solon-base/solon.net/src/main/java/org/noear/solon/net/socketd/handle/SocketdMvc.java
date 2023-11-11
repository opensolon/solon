package org.noear.solon.net.socketd.handle;

/**
 * Socket.D Mvc 工具类
 *
 * @author noear
 * @since 2.0
 */
public class SocketdMvc {
    private static final SocketdToMvc listener = new SocketdToMvc();

    /**
     * 获取转换监听
     * */
    public static SocketdToMvc getListener() {
        return listener;
    }
}
