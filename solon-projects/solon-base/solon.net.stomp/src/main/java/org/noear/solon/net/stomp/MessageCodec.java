package org.noear.solon.net.stomp;


import java.util.function.Consumer;


/**
 * 消息编解码器
 *
 * @author limliu
 * @since 2.7
 */
public interface MessageCodec {

    /**
     * 编码
     *
     * @param input Stomp 消息
     * @return 编码后的文本
     */
    String encode(Message input);

    /**
     * 解码
     *
     * @param input 输入
     * @param out   输出
     */
    void decode(String input, Consumer<Message> out);

}

