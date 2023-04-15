package org.noear.solon.aot;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.wrap.FieldWrap;
import org.noear.solon.core.wrap.MethodWrap;

/**
 * aot 阶段，对{@link org.noear.solon.core.AopContext}中的bean、方法、字段添加 native 元数据
 *
 * @author songyinyin
 * @since 2.2
 */
public interface AopContextNativeProcessor {

    /**
     * aot 阶段，对所有的 bean 注册 native 元数据
     *
     * @param nativeMetadata native 运行时元信息
     * @param beanWrap       bean 包装
     */
    void processBean(RuntimeNativeMetadata nativeMetadata, BeanWrap beanWrap);

    /**
     * aot 阶段，对bean中的方法，注册 native 元数据
     *
     * @param nativeMetadata native 运行时元信息
     * @param methodWrap     method 包装
     */
    void processMethod(RuntimeNativeMetadata nativeMetadata, MethodWrap methodWrap);

    /**
     * aot 阶段，对bean中字段，注册 native 元数据
     *
     * @param nativeMetadata native 运行时元信息
     * @param fieldWrap      field 包装
     */
    void processField(RuntimeNativeMetadata nativeMetadata, FieldWrap fieldWrap);
}
