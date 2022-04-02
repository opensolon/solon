package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.CloudJobInterceptor;
import org.noear.solon.cloud.service.CloudJobService;

/**
 * @author noear
 * @since 1.6
 */
public interface CloudJobServiceManager extends CloudJobService {
    /**
     * 获取任务拦截器
     * */
    CloudJobInterceptor getJobInterceptor();
}
