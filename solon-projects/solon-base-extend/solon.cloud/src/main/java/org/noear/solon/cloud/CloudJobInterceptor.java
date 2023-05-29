package org.noear.solon.cloud;

import org.noear.solon.cloud.model.Job;

/**
 * 云工作拦截器
 *
 * @author noear
 * @since 1.6
 */
public interface CloudJobInterceptor {
    void doIntercept(Job job, CloudJobHandler handler) throws Throwable;
}
