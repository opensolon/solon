package org.noear.solon.serialization.hessian;

import org.noear.solon.serialization.BytesSerializerRender;
import org.noear.solon.serialization.ContextSerializer;

//不要要入参，方便后面多视图混用
//
public class HessianRender extends BytesSerializerRender {
    private HessianBytesSerializer serializer = new HessianBytesSerializer();

    @Override
    public ContextSerializer<byte[]> getSerializer() {
        return serializer;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}