package org.noear.solon.serialization;

import org.noear.solon.core.handle.Render;

/**
 * Json 渲染器工厂
 *
 * @author noear
 * @since 1.5
 */
public interface JsonRenderFactory {
    /**
     * 添加数据转换器
     * */
    <T> void addConvertor(Class<T> clz, JsonStringConverter<T> converter);

    /**
     * 添加数据转换器
     * */
    <T> void addConvertor(Class<T> clz, JsonLongConverter<T> converter);

    /**
     * 创建渲染器
     * */
    Render create();
}
