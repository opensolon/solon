package org.noear.solon.api.socket;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SocketMessage {
    /**
     * 消息key
     * */
    public String key;
    /**
     * 资源描述
     * */
    public String resourceDescriptor;
    /**
     * 消息内容
     * */
    public byte[] content;
    /**
     * 消息编码
     * */
    public Charset charset = StandardCharsets.UTF_8;
}
