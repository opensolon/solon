package org.noear.solon.serialization;

import org.noear.solon.core.convert.Converter;

/**
 * 数字转换器
 *
 * @author noear
 * @since 1.5
 * @deprecated 2.4
 */
@Deprecated
@FunctionalInterface
public interface JsonConverter<T> extends Converter<T,Object> {

}
