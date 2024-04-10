package org.noear.solon.cloud.impl;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.CloudJobInterceptor;
import org.noear.solon.cloud.service.CloudJobService;
import org.noear.solon.core.util.RankEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author noear
 * @since 1.6
 */
public class CloudJobServiceManagerImpl implements CloudJobServiceManager {
    private final CloudJobService service;
    private List<RankEntity<CloudJobInterceptor>> jobInterceptors = new ArrayList<>();

    public CloudJobServiceManagerImpl(CloudJobService service) {
        this.service = service;
        Solon.context().subWrapsOfType(CloudJobInterceptor.class, bw -> {
            addJobInterceptor(bw.index(), bw.raw());
        });
    }

    /**
     * 添加任务拦截器
     *
     * @since 2.7
     */
    @Override
    public void addJobInterceptor(int index, CloudJobInterceptor jobInterceptor) {
        jobInterceptors.add(new RankEntity<>(jobInterceptor, index));
    }

    /**
     * 获取任务拦截器
     */
    @Override
    public List<RankEntity<CloudJobInterceptor>> getJobInterceptors() {
        return Collections.unmodifiableList(jobInterceptors);
    }

    /**
     * 注册任务
     */
    @Override
    public boolean register(String name, String cron7x, String description, CloudJobHandler handler) {
        return service.register(name, cron7x, description, handler);
    }

    /**
     * 是否已注册
     */
    @Override
    public boolean isRegistered(String name) {
        return service.isRegistered(name);
    }
}
