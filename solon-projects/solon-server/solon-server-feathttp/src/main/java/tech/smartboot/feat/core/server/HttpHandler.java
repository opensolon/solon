/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.server;

import tech.smartboot.feat.core.server.impl.HttpEndpoint;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP请求处理器接口
 * <p>
 * 该接口定义了处理HTTP请求的核心方法，支持同步和异步两种处理模式。
 * 实现此接口的类负责接收HTTP请求并生成相应的响应。
 * </p>
 * <p>
 * 同步处理模式：直接实现{@link #handle(HttpRequest)}方法，在当前线程中完成请求处理。
 * 异步处理模式：重写{@link #handle(HttpRequest, CompletableFuture)}方法，将请求处理逻辑放在独立线程中执行，
 * 并在处理完成后手动完成CompletableFuture。
 * </p>
 *
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public interface HttpHandler {
    /**
     * HTTP请求头部完成时的回调方法
     * <p>
     * 当HTTP请求的头部信息接收完成时，该方法会被调用。
     * 实现类可以重写此方法以在请求头部完成时执行特定逻辑，例如：
     * <ul>
     *   <li>检查请求头部信息</li>
     *   <li>执行预处理操作</li>
     *   <li>记录请求信息</li>
     * </ul>
     * </p>
     *
     * @param request HTTP端点对象，包含请求的详细信息
     * @throws IOException 如果在处理过程中发生I/O错误
     */
    default void onHeaderComplete(HttpEndpoint request) throws IOException {
    }

    /**
     * 异步处理HTTP请求的方法
     * <p>
     * 此方法提供了异步处理HTTP请求的能力。默认实现会调用同步处理方法{@link #handle(HttpRequest)}，
     * 并在处理完成后自动完成CompletableFuture。
     * </p>
     * <p>
     * 要实现真正的异步处理，实现类应该重写此方法，并：
     * <ol>
     *   <li>将请求处理逻辑放在独立的线程中执行</li>
     *   <li>处理完成后手动调用completableFuture.complete(result)完成Future</li>
     *   <li>如果处理过程中发生异常，调用completableFuture.completeExceptionally(throwable)传递异常</li>
     * </ol>
     * </p>
     * <p>
     * 示例：
     * <pre>
     * {@code
     * ExecutorService executorService = Executors.newFixedThreadPool(10);
     *
     * @Override
     * public void handle(HttpRequest request, CompletableFuture<Object> future) throws Throwable {
     *     executorService.execute(() -> {
     *         try {
     *             // 执行耗时操作
     *             Object result = processRequest(request);
     *             // 处理完成，手动完成CompletableFuture
     *             future.complete(result);
     *         } catch (Exception e) {
     *             // 处理异常，传递给CompletableFuture
     *             future.completeExceptionally(e);
     *         }
     *     });
     * }
     * }
     * </pre>
     * </p>
     *
     * @param request           HTTP请求对象
     * @param completableFuture 用于异步处理的CompletableFuture对象，处理完成后需要手动完成
     * @throws Throwable 如果在处理过程中发生任何异常
     */
    default void handle(HttpRequest request, CompletableFuture<Void> completableFuture) throws Throwable {
        try {
            handle(request);
        } finally {
            completableFuture.complete(null);
        }
    }

    /**
     * 同步处理HTTP请求的方法
     * <p>
     * 此方法用于同步处理HTTP请求。实现类必须实现此方法以处理传入的HTTP请求并生成响应。
     * 该方法在当前线程中执行，如果包含耗时操作，会阻塞当前线程直到处理完成。
     * </p>
     * <p>
     * 对于需要执行耗时操作的场景，建议使用异步处理方法{@link #handle(HttpRequest, CompletableFuture)}。
     * </p>
     *
     * @param request HTTP请求对象，包含请求的详细信息和响应对象
     * @throws Throwable 如果在处理过程中发生任何异常
     */
    void handle(HttpRequest request) throws Throwable;

    /**
     * TCP连接关闭时的回调方法
     * <p>
     * 当与客户端的TCP连接断开时，该方法会被调用。
     * 实现类可以重写此方法以在连接关闭时执行清理操作，例如：
     * <ul>
     *   <li>释放资源</li>
     *   <li>记录连接关闭信息</li>
     *   <li>执行统计或监控操作</li>
     * </ul>
     * </p>
     *
     * @param request HTTP端点对象，包含已关闭连接的详细信息
     */
    default void onClose(HttpEndpoint request) {
    }
}
