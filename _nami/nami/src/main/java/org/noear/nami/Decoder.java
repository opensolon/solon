package org.noear.nami;

import org.noear.nami.common.Result;

import java.lang.reflect.Type;

/**
 * 解码器
 *
 * @author noear
 * @since 1.2
 * */
public interface Decoder extends Filter {
    /**
     * 编码
     * */
    String enctype();

    /**
     * 反序列化
     * */
    <T> T decode(Result rst, Type clz);
}
