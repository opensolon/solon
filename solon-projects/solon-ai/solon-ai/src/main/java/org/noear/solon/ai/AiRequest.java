package org.noear.solon.ai;

import java.util.function.Consumer;

/**
 * @author noear 2025/1/28 created
 */
public interface AiRequest<O extends AiOptions, Req extends AiRequest, Resp extends AiResponse> {
    /**
     * 选项
     */
    Req options(O options);

    /**
     * 调用
     */
    Resp call();

    /**
     * 流响应
     */
    void stream(Consumer<Resp> consumer);
}
