package org.noear.solon.serialization;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.serialize.Serializer;

import java.io.IOException;

/**
 * 通用上下文接口序列化器
 *
 * @author noear
 * @since 2.8
 */
public interface ContextSerializer<T> extends Serializer<T> {
    /**
     * 获取内容类型
     */
    String getContentType();

    /**
     * 匹配
     *
     * @param ctx  上下文
     * @param mime
     */
    boolean matched(Context ctx, String mime);

    /**
     * 序列化到
     */
    void serializeToBody(Context ctx, Object data) throws IOException;

    /**
     * 反序列化从
     *
     * @param ctx 上下文
     */
    Object deserializeFromBody(Context ctx) throws IOException;
}
