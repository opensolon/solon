package org.noear.solon.serialization.protostuff;

import org.noear.solon.serialization.BytesSerializerRender;
import org.noear.solon.serialization.ContextSerializer;

/**
 * Protostuff 渲染器
 *
 * @author noear
 * @since 1.2
 * @since 2.8
 */
public class ProtostuffRender extends BytesSerializerRender {
    private ProtostuffBytesSerializer serializer = new ProtostuffBytesSerializer();


    @Override
    public ContextSerializer<byte[]> getSerializer() {
        return serializer;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
