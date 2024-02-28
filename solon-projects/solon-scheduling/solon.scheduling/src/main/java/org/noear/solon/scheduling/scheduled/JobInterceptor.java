package org.noear.solon.scheduling.scheduled;

/**
 * 任务拦截器
 *
 * @author noear
 * @since 2.7
 */
public interface JobInterceptor {
    /**
     * 拦截
     *
     * @param job     任务
     * @param handler 处理器
     */
    void doIntercept(Job job, JobHandler handler) throws Throwable;
}
