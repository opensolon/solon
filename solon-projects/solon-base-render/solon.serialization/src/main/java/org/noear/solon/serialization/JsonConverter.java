package org.noear.solon.serialization;

/**
 * 数字转换器
 *
 * @author noear
 * @since 1.5
 */
@FunctionalInterface
public interface JsonConverter<T> {
    /**
     * 转为数字
     * */
    Object convert(T source);
}
