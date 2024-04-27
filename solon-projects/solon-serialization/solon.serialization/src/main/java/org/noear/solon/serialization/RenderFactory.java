package org.noear.solon.serialization;

import org.noear.solon.core.handle.Render;

/**
 * 渲染器工厂
 *
 * @author noear
 * @since 2.7
 */
public interface RenderFactory {
    /**
     * 创建渲染器
     */
    Render create();
}
