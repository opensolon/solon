package org.noear.solon.serialization.fury;

import org.noear.solon.serialization.BytesSerializerRender;
import org.noear.solon.serialization.ContextSerializer;

/**
 * @author noear
 * @since 2.5
 * */
public class FuryRender extends BytesSerializerRender {
    private final FuryBytesSerializer serializer = new FuryBytesSerializer();

    @Override
    public ContextSerializer<byte[]> getSerializer() {
        return serializer;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
