package org.noear.solon.serialization;

import java.io.IOException;

/**
 * 字符串序列化器
 *
 * @author noear
 * @since 1.5
 * @deprecated 2.8
 */
@Deprecated
@FunctionalInterface
public interface StringSerializer {
    /**
     * 序列化为字符串
     * */
    String serialize(Object source) throws IOException;
}
