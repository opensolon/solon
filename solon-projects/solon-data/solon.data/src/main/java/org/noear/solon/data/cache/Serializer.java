package org.noear.solon.data.cache;

/**
 * 对象序列化接口
 *
 * @author noear
 * @since 1.5
 * */
public interface Serializer<T> {
    /**
     * 名称
     */
    String name();

    /**
     * 序列化
     */
    T serialize(Object fromObj) throws Exception;

    /**
     * 反序列化
     * @deprecated 2.5
     */
    @Deprecated
    default Object deserialize(T dta) throws Exception {
        return deserialize(dta, Object.class);
    }

    /**
     * 反序列化
     */
    Object deserialize(T dta, Class<?> toClz) throws Exception;
}
