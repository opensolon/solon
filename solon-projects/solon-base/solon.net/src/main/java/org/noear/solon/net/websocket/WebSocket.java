package org.noear.solon.net.websocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * WebSocket 会话接口
 *
 * @author noear
 * @since 2.6
 */
public interface WebSocket {
    /**
     * 获取会话Id
     * */
    String getSid();

    /**
     * 是否有效
     */
    boolean isValid();

    /**
     * 是否安全
     */
    boolean isSecure();

    /**
     * 获取请求地址
     * */
    String getUrl();

    /**
     * 获取握手路径
     */
    String getPath();

    /**
     * 设置新路径
     */
    void setPathNew(String pathNew);

    /**
     * 获取参数字典
     */
    Map<String, String> getParamMap();

    /**
     * 获取参数
     *
     * @param name 参数名
     */
    String getParam(String name);

    /**
     * 获取参数或默认值
     *
     * @param name 参数名
     * @param def  默认值
     */
    String getParamOrDefault(String name, String def);

    /**
     * 添加参数
     *
     * @param name  名字
     * @param value 值
     */
    String putParam(String name, String value);

    /**
     * 获取远程地址
     */
    InetSocketAddress getRemoteAddress() throws IOException;

    /**
     * 获取本地地址
     */
    InetSocketAddress getLocalAddress() throws IOException;

    /**
     * 设置附件
     */
    <T> void setAttachment(T attachment);

    /**
     * 获取附件
     */
    <T> T getAttachment();

    /**
     * 获取所有属性
     */
    Map<String, Object> getAttrMap();

    /**
     * 获取属性
     *
     * @param name 名字
     */
    <T> T getAttr(String name);

    /**
     * 获取属性或默认值
     *
     * @param name 名字
     * @param def  默认值
     */
    <T> T getAttrOrDefault(String name, T def);

    /**
     * 设置属性
     *
     * @param name  名字
     * @param value 值
     */
    <T> void setAttr(String name, T value);

    /**
     * 发送文本
     *
     * @param text 文本
     */
    void send(String text);

    /**
     * 发送字节
     *
     * @param binary 二进制
     */
    void send(ByteBuffer binary);

    /**
     * 关闭
     */
    void close();
}