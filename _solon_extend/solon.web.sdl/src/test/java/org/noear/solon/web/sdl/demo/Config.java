package org.noear.solon.web.sdl.demo;

import org.noear.redisx.RedisClient;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.validation.annotation.LoginedChecker;
import org.noear.solon.web.sdl.SdlLoginedChecker;
import org.noear.solon.web.sdl.SdlStorage;
import org.noear.solon.web.sdl.impl.SdlStorageOfLocal;

/**
 * 主要是为了构建： SsoService 和 SsoLoginedChecker
 *
 * @author noear 2023/4/5 created
 */
@Configuration
public class Config {
    // 用本地内存存储，一般用于临时测试
    @Bean
    public SdlStorage sdlStorage1() {
        return new SdlStorageOfLocal();
    }

    @Bean
    public LoginedChecker sdlLoginedChecker() {
        return new SdlLoginedChecker();
    }
}
