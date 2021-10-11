package org.noear.solon.serialization;

/**
 * 数字转换器
 *
 * @author noear
 * @since 1.5
 */
@FunctionalInterface
public interface StringConverter<T> {
    /**
     * 转为数字
     * */
    String convert(T source);
}
