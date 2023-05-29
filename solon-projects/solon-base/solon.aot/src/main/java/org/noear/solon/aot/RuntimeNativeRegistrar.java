package org.noear.solon.aot;

import org.noear.solon.core.AopContext;

/**
 * aot 阶段，注册 native 运行时的元信息，其实现类需要是一个 solon bean
 *
 * @author songyinyin
 * @since 2023/4/13 19:02
 */
public interface RuntimeNativeRegistrar {

    /**
     * aot 阶段，注册 native 运行时元信息
     *
     * @param context  上下文
     * @param metadata 原生运行时元信息
     */
    void register(AopContext context, RuntimeNativeMetadata metadata);
}
