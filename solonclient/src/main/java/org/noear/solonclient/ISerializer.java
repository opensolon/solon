package org.noear.solonclient;

public interface ISerializer {
    String stringify(Object obj);
    String serialize(Object obj);
    <T> T deserialize(Result rst, Class<T> clz);
}
