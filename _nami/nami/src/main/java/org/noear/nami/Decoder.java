package org.noear.nami;

import java.lang.reflect.Type;

/**
 * Nami - 解码器
 *
 * @author noear
 * @since 1.2
 * */
public interface Decoder {
    /**
     * 编码
     * */
    String enctype();

    /**
     * 反序列化
     * */
    <T> T decode(Result rst, Type clz);

    /**
     * 预处理
     * */
    void pretreatment(NamiContext ctx);
}
