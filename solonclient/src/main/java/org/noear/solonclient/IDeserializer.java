package org.noear.solonclient;

public interface IDeserializer {
    /**
     * 编码
     * */
    Enctype enctype();

    /**
     * 反序列化
     * */
    <T> T deserialize(Result rst, Class<T> clz);
}
