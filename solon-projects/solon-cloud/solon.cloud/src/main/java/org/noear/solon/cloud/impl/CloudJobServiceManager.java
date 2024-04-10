package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.CloudJobInterceptor;
import org.noear.solon.cloud.service.CloudJobService;
import org.noear.solon.core.util.RankEntity;

import java.util.List;

/**
 * @author noear
 * @since 1.6
 */
public interface CloudJobServiceManager extends CloudJobService {
    /**
     * 添加任务拦截器
     *
     * @since 2.7
     */
    void addJobInterceptor(int index, CloudJobInterceptor jobInterceptor);

    /**
     * 获取任务拦截器
     */
    List<RankEntity<CloudJobInterceptor>> getJobInterceptors();
}
