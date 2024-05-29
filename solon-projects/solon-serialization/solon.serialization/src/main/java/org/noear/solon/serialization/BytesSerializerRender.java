package org.noear.solon.serialization;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;

import java.util.Base64;

/**
 * 字节组序列化渲染器
 *
 * @author noear
 * @since 2.8
 */
public abstract class BytesSerializerRender implements Render {
    /**
     * 获取序列化器
     */
    public abstract ContextSerializer<byte[]> getSerializer();

    /**
     * 获取渲染器名字
     */
    @Override
    public String getName() {
        return this.getClass().getSimpleName() + "#" + getSerializer().name();
    }

    /**
     * 匹配检测
     */
    @Override
    public boolean matched(Context ctx, String accept) {
        return getSerializer().matched(ctx, accept);
    }

    /**
     * 渲染并返回
     */
    @Override
    public String renderAndReturn(Object data, Context ctx) throws Throwable {
        byte[] tmp = getSerializer().serialize(data);
        return Base64.getEncoder().encodeToString(tmp);
    }

    /**
     * 渲染并输出
     */
    @Override
    public void render(Object data, Context ctx) throws Throwable {
        if (SerializationConfig.isOutputMeta()) {
            ctx.headerAdd("solon.serialization", getName());
        }

        getSerializer().serializeToBody(ctx, data);
    }
}
