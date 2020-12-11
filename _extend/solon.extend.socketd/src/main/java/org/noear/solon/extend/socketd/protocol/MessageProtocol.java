package org.noear.solon.extend.socketd.protocol;

import org.noear.solon.core.message.Message;

import java.nio.ByteBuffer;

public interface MessageProtocol {
    ByteBuffer encode(Message message);
    Message decode(ByteBuffer buffer);
}
