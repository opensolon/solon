package org.noear.solon.core.handle;

/**
 * 动作加载器
 *
 * @author noear
 * @since 2.7
 */
public interface ActionLoader {
    /**
     * 映射
     */
    String mapping();

    /**
     * 加载
     *
     * @param slots 插槽（即接收者）
     */
    void load(HandlerSlots slots);
}
