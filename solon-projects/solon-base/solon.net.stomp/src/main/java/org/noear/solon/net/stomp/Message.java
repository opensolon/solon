package org.noear.solon.net.stomp;


import java.util.List;

/**
 * 消息
 *
 * @author limliu
 * @since 2.7
 */
public interface Message {

    /**
     * 添加head
     *
     * @param key 键, key参考#Header
     * @param val 值
     * @return
     */
    Message addHeader(String key, String val);

    /**
     * 获取head集合
     *
     * @return
     */
    List<Header> getHeaderAll();

    /**
     * 获取head
     *
     * @param key 参考#Header
     * @return
     */
    String getHeader(String key);

    /**
     * 获取消息内容
     *
     * @return
     */
    String getPayload();

    /**
     * 获取命令, 如send...等
     * 参考#Commands
     *
     * @return
     */
    String getCommand();

}
