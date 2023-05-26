package com.fujieid.jap.ids.solon.configurations;

import com.fujieid.jap.ids.context.IdsContext;
import com.fujieid.jap.ids.solon.IdsCacheImpl;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import com.fujieid.jap.ids.solon.services.IdsClientDetailServiceImpl;
import com.fujieid.jap.ids.solon.services.IdsIdentityServiceImpl;
import com.fujieid.jap.ids.solon.services.IdsUserServiceImpl;
import org.noear.solon.core.AopContext;

@Configuration
public class JapConfig {

    @Inject
    AopContext context;

    @Bean
    public void ids(@Inject IdsContext idsContext) {
        // 由于 Solon 的 ClassLoader 机制,
        // ServiceLoader 并不会正常运行
        // 白高兴了...
        idsContext.setCache(context.getBeanOrNew(IdsCacheImpl.class));
        idsContext.setClientDetailService(context.getBeanOrNew(IdsClientDetailServiceImpl.class));
        idsContext.setIdentityService(context.getBeanOrNew(IdsIdentityServiceImpl.class));
        idsContext.setUserService(context.getBeanOrNew(IdsUserServiceImpl.class));
    }

}
