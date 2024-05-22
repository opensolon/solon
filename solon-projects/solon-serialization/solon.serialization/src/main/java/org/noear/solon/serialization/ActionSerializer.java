package org.noear.solon.serialization;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.serialize.Serializer;

import java.io.IOException;

/**
 * @author noear
 * @since 2.8
 */
public interface ActionSerializer<T> extends Serializer<T> {
    /**
     * 反序列化
     *
     * @param ctx 上下文
     */
    Object deserializeBody(Context ctx) throws IOException;
}
