package org.noear.solon.aot;

import org.noear.solon.core.AppContext;

/**
 * Aot 阶段，对 {@link org.noear.solon.core.AppContext} 中的类、方法、字段、实体、代理等...添加原生元数据
 *
 * @author songyinyin
 * @since 2.2
 */
public interface AppContextNativeProcessor {


    /**
     * 处理（生成配置、代理等...）
     *
     * @param context  上下文
     * @param settings 运行设置
     * @param metadata 元信息对象
     */
    void process(AppContext context, Settings settings, RuntimeNativeMetadata metadata);
}
