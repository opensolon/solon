package org.noear.solon.serialization;

import java.io.IOException;

/**
 * 字符串序列化器
 *
 * @author noear
 * @since 1.5
 * @deprecated 2.8
 * @removal true
 */
@Deprecated
public interface StringSerializer extends org.noear.solon.core.serialize.Serializer<String> {
    /**
     * 名字
     */
    default String name() {
        return this.getClass().getSimpleName();
    }

    /**
     * 序列化为字符串
     */
    String serialize(Object fromObj) throws IOException;


    default Object deserialize(String data, Class<?> toClz) throws IOException {
        return null;
    }
}