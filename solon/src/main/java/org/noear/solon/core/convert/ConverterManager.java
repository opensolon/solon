package org.noear.solon.core.convert;

import org.noear.solon.core.util.GenericUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 转换管理器
 *
 * @author noear
 * @since 2.4
 */
public class ConverterManager {
    Map<Type, Map<Type, Converter>> converters = new HashMap<>();

    /**
     * 注册转换器
     *
     * @param converter 转换器
     */
    public <S, T> void register(Converter<S, T> converter) {
        Map<String, Type> genericInfo = GenericUtil.getGenericInfo(converter.getClass());
        Type sourceType = null;
        Type tagertType = null;

        if (genericInfo != null) {
            sourceType = genericInfo.get("S");
            tagertType = genericInfo.get("T");
        }

        if (sourceType == null) {
            sourceType = Object.class;
        }

        if (tagertType == null) {
            tagertType = Object.class;
        }

        Map<Type, Converter> tmp = converters.get(sourceType);
        if (tmp == null) {
            tmp = new HashMap<>();
            converters.put(sourceType, tmp);
        }

        tmp.put(tagertType, converter);
    }

    /**
     * 查找转换器
     *
     * @param sourceType 源类型
     * @param tagertType 目标类型
     */
    public <S, T> Converter<S, T> find(Class<S> sourceType, Class<T> tagertType) {
        Map<Type, Converter> tmp = converters.get(sourceType);
        if (tmp == null) {
            return null;
        } else {
            return tmp.get(tagertType);
        }
    }
}
