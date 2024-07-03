package org.noear.solon.core.serialize;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 序列化器
 *
 * @author noear
 * @since 2.8
 */
public interface Serializer<T> {
    /**
     * 名字
     */
    String name();

    /**
     * 序列化
     */
    T serialize(Object fromObj) throws IOException;

    /**
     * 反序列化
     */
    Object deserialize(T data, Type toType) throws IOException;
}
