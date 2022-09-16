package org.noear.solon.socketd.protocol;

import org.noear.solon.core.message.Message;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 消息协议
 *
 * @author noear
 * @since 1.2
 * */
public interface MessageProtocol {
    ByteBuffer encode(Message message) throws Exception;

    Message decode(ByteBuffer buffer) throws Exception;
}
