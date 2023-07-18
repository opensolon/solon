package org.noear.solon.core.convert;

import org.noear.solon.core.exception.ConvertException;

/**
 * 转换器
 *
 * @author noear
 * @since 2.4
 */
@FunctionalInterface
public interface Converter<S,T> {
    /**
     * 转换
     * */
    T convert(S value) throws ConvertException;
}
