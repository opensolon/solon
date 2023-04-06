package org.noear.solon.web.sso.impl;

import org.noear.redisx.RedisClient;
import org.noear.solon.Utils;
import org.noear.solon.web.sso.SsoStorage;

import java.io.Serializable;

/**
 * 单点登录数据服务 Redis 实现
 *
 * @author noear
 * @since 2.2
 */
public class SsoStorageOfRedis implements SsoStorage {
    RedisClient storage;

    public SsoStorageOfRedis(RedisClient redisClient) {
        this.storage = redisClient;
    }

    /**
     * 更新单点登录标识
     */
    @Override
    public String updateUserSsoKey(Serializable userId) {
        String storageKey = SsoStorage.SESSION_SSO_KEY + ":" + userId;
        String userSsoKey = Utils.guid();

        //设置用户的单点登录标识（当前会话 与 用户的'单点登录标识'，说明当前会话不是最新的登录）
        storage.open(ru -> {
            ru.key(storageKey).persist().set(userSsoKey);
        });

        return userSsoKey;
    }

    /**
     * 移除单点登录标识
     * */
    @Override
    public void removeUserSsoKey(Serializable userId) {
        String storageKey = SsoStorage.SESSION_SSO_KEY + ":" + userId;

        storage.open(ru -> {
            ru.key(storageKey).delete();
        });
    }

    /**
     * 获取单点登录标识
     */
    @Override
    public String getUserSsoKey(Serializable userId) {
        String storageKey = SsoStorage.SESSION_SSO_KEY + ":" + userId;

        return storage.openAndGet(ru -> ru.key(storageKey).get());
    }
}