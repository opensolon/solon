package org.noear.solon.ai;

import org.reactivestreams.Publisher;

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
    Resp call() throws Throwable;

    /**
     * 流响应
     */
    Publisher<Resp> stream();
}
