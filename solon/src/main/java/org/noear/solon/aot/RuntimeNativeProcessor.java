package org.noear.solon.aot;

import org.noear.solon.core.AopContext;

/**
 * aot 阶段，生成 native 运行时的元信息，其实现类需要为 solon bean
 *
 * @author songyinyin
 * @since 2023/4/13 19:02
 */
public interface RuntimeNativeProcessor {

    void process(AopContext context, RuntimeNativeMetadata nativeMetadata);
}
