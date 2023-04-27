package org.noear.solon.aot;

import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.wrap.FieldWrap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * aot 阶段，对 {@link org.noear.solon.core.AopContext} 中的bean、方法、字段添加 native 元数据
 *
 * @author songyinyin
 * @since 2.2
 */
public interface BeanNativeProcessor {

    /**
     * aot 阶段，对所有的 bean 注册 native 元数据
     *
     * @param nativeMetadata native 运行时元信息
     * @param clazz          clazz
     * @param supportProxy   是否支持代理
     */
    void processBean(RuntimeNativeMetadata nativeMetadata, Class<?> clazz, boolean supportProxy);

    default void processBean(RuntimeNativeMetadata nativeMetadata, Class<?> clazz) {
        processBean(nativeMetadata, clazz, false);
    }


    default void processBeanMethods(RuntimeNativeMetadata nativeMetadata, Class<?> clazz){
        ClassWrap clzWrap = ClassWrap.get(clazz);

        // 处理函数
        for (Method method : clzWrap.getMethods()) {
            processMethod(nativeMetadata, method);
        }
    }

    default void processBeanFields(RuntimeNativeMetadata nativeMetadata, Class<?> clazz){
        ClassWrap clzWrap = ClassWrap.get(clazz);

        // 处理字段
        Map<String, FieldWrap> fieldAllWraps = clzWrap.getFieldAllWraps();
        for (FieldWrap fieldWrap : fieldAllWraps.values()) {
            processField(nativeMetadata, fieldWrap.field);
        }
    }

    /**
     * aot 阶段，对 bean 中的方法，注册 native 元数据
     *
     * @param nativeMetadata native 运行时元信息
     * @param method         method
     */
    void processMethod(RuntimeNativeMetadata nativeMetadata, Method method);

    /**
     * aot 阶段，对 bean 中字段，注册 native 元数据
     *
     * @param nativeMetadata native 运行时元信息
     * @param field          field
     */
    void processField(RuntimeNativeMetadata nativeMetadata, Field field);
}
