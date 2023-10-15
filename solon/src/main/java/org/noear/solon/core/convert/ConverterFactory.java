package org.noear.solon.core.convert;

/**
 * 转换器工厂
 *
 * @author noear
 * @since 2.5
 */
public interface ConverterFactory<S, R> {
    <T extends R> Converter<S, T> getConverter(Class<T> targetType);
}

