package org.noear.solon.serialization;

import java.io.IOException;

/**
 * 序列化
 *
 * @author noear
 * @since 2.8
 */
public interface Serializer<T> {
    /**
     * 序列化
     *
     * @param obj 对象
     */
    T serialize(Object obj) throws IOException;

    /**
     * 反序列化
     *
     * @param data 数据
     * @param clz  类
     */
    Object deserialize(T data, Class<?> clz) throws IOException;
}
