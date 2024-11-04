package org.noear.solon.serialization.sbe;

import org.noear.solon.serialization.BytesSerializerRender;
import org.noear.solon.serialization.ContextSerializer;

/**
 * @author noear
 * @since 3.0
 */
public class SbeRender extends BytesSerializerRender {
    private final SbeBytesSerializer serializer = new SbeBytesSerializer();

    /**
     * 获取序列化器
     */
    @Override
    public ContextSerializer<byte[]> getSerializer() {
        return serializer;
    }

    /**
     * 获取渲染器名字
     */
    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }
}