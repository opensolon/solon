package org.noear.solon.ai;

/**
 * @author noear 2025/2/10 created
 */
public interface AiChoice<Msg> {
    /**
     * 顺序位
     */
    int index();

    /**
     * 获取消息
     */
    Msg getMessage();
}