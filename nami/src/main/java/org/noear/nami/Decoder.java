package org.noear.nami;

/**
 * 解码器
 * */
public interface Decoder extends Filter {
    /**
     * 编码
     * */
    Enctype enctype();

    /**
     * 反序列化
     * */
    <T> T decode(Result rst, Class<T> clz);
}
