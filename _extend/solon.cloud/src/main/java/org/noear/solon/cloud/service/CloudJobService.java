package org.noear.solon.cloud.service;

import org.noear.solon.core.handle.Handler;

/**
 * @author noear
 * @since 1.3
 */
public interface CloudJobService {
    /**
     * 注册任务
     */
    boolean register(String name, Handler handler);

    /**
     * 是否已注册
     * */
    boolean isRegistered(String name);
}
