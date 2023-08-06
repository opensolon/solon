package org.noear.solon.serialization;

import java.io.IOException;

/**
 * 字符串序列化器
 *
 * @author noear
 * @since 1.5
 */
@FunctionalInterface
public interface StringSerializer<T> {
    /**
     * 序列化为字符串
     * */
    String serialize(T source) throws IOException;
}
