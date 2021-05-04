package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudJobHandler;

/**
 * @author noear
 * @since 1.3
 */
public interface CloudJobService {
    /**
     * 注册任务
     */
    boolean register(String job, CloudJobHandler handler);
}
