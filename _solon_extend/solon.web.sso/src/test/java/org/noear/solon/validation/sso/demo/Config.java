package org.noear.solon.validation.sso.demo;

import org.noear.redisx.RedisClient;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.validation.annotation.LoginedChecker;
import org.noear.solon.web.sso.SsoLoginedChecker;
import org.noear.solon.web.sso.SsoService;
import org.noear.solon.web.sso.SsoServiceImpl;

/**
 * 主要是为了构建： SsoService 和 SsoLoginedChecker
 *
 * @author noear 2023/4/5 created
 */
@Configuration
public class Config {
    @Bean
    public SsoService ssoService(@Inject("${demo.redis}") RedisClient redisClient) {
        // RedisClient 可以通过配置自己构建
        // 如果使用的是别的Redis框架，可以自己实现SsoService（只有两个接口）
        return new SsoServiceImpl(redisClient);
    }

    @Bean
    public LoginedChecker ssoLoginedChecker(@Inject SsoService ssoService) {
        return new SsoLoginedChecker(ssoService);
    }
}
