package org.noear.solon.plugin.jap.ids.configurations;

import com.fujieid.jap.core.context.JapContext;
import com.fujieid.jap.ids.context.IdsContext;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.plugin.jap.ids.services.IdsClientDetailServiceImpl;
import org.noear.solon.plugin.jap.ids.services.IdsIdentityServiceImpl;
import org.noear.solon.plugin.jap.ids.services.IdsUserServiceImpl;

@Configuration
public class JapConfig {

    @Bean
    public void ids(@Inject IdsContext context) {
        // 由于 Solon 的 ClassLoader 机制,
        // ServiceLoader 并不会正常运行
        // 白高兴了...
        context.setClientDetailService(new IdsClientDetailServiceImpl());
        context.setIdentityService(new IdsIdentityServiceImpl());
        context.setUserService(new IdsUserServiceImpl());
    }

}
