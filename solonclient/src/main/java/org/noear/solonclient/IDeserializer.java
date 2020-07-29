package org.noear.solonclient;

public interface IDeserializer {
    <T> T deserialize(Result rst, Class<T> clz);
}
