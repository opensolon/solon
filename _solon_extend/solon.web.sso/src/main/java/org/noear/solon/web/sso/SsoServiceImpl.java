package org.noear.solon.web.sso;

import org.noear.redisx.RedisClient;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;

/**
 * 单点登录数据服务默认实现（可以定制实现）
 *
 * @author noear
 * @since 2.2
 */
public class SsoServiceImpl implements SsoService {
    RedisClient redisClient;

    public SsoServiceImpl(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    /**
     * 更新单点登录标识
     */
    @Override
    public void updateUserSsoKey(Context ctx, long userId) {
        String userSsoKey = Utils.guid();

        ctx.sessionSet(SsoService.SESSION_USER_ID, userId);

        //设置当前会话的单点登录标识
        ctx.sessionSet(SsoService.SESSION_SSO_KEY, userSsoKey);

        //设置用户的单点登录标识（当前会话 与 用户的'单点登录标识'，说明当前会话不是最新的登录）
        redisClient.open(ru -> {
            ru.key(SsoService.SESSION_SSO_KEY + ":" + userId).persist().set(userSsoKey);
        });
    }

    /**
     * 获取单点登录标识
     */
    @Override
    public String getUserSsoKey(long userId) {
        return redisClient.openAndGet(ru -> ru.key(SsoService.SESSION_SSO_KEY + ":" + userId).get());
    }
}