package org.noear.solon.validation.sso.demo;

import org.noear.redisx.RedisClient;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.validation.annotation.LoginedChecker;
import org.noear.solon.web.sso.SsoLoginedChecker;
import org.noear.solon.web.sso.SsoUtil;
import org.noear.solon.web.sso.impl.SsoStorageOfLocal;
import org.noear.solon.web.sso.impl.SsoStorageOfRedis;

/**
 * 主要是为了构建： SsoService 和 SsoLoginedChecker
 *
 * @author noear 2023/4/5 created
 */
@Configuration
public class Config {
    @Bean
    public LoginedChecker ssoInit(@Inject("${demo.redis}") RedisClient redisClient) {
        SsoUtil.setStorage(new SsoStorageOfRedis(redisClient));
        return new SsoLoginedChecker();
    }

// 用本地内存存府，一般用于临时测试
//    @Bean
//    public LoginedChecker ssoInit() {
//        SsoUtil.setStorage(new SsoStorageOfLocal());
//        return new SsoLoginedChecker();
//    }
}
