package org.noear.solon.core.handle;

import java.io.IOException;

/**
 * 通用上下文接口 - 异步监听
 *
 * @author noear
 * @since 2.3
 */
public interface ContextAsyncListener {
    /**
     * 开始
     *
     * @param ctx 请求上下文
     */
    void onStart(Context ctx) throws IOException;

    /**
     * 完成
     *
     * @param ctx 请求上下文
     */
    void onComplete(Context ctx) throws IOException;

    /**
     * 超时
     *
     * @param ctx 请求上下文
     */
    void onTimeout(Context ctx) throws IOException;

    /**
     * 出错
     *
     * @param ctx 请求上下文
     * @param e   异步
     */
    void onError(Context ctx, Throwable e) throws IOException;
}
