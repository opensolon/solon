package cn.dev33.satoken.solon.sso;

import cn.dev33.satoken.config.SaSsoConfig;
import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.SaSsoProcessor;
import cn.dev33.satoken.sso.SaSsoTemplate;
import cn.dev33.satoken.sso.SaSsoUtil;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.bean.InitializingBean;

/**
 * @author noear
 * @since 2.0
 */
@Condition(onClass = SaSsoManager.class)
@Configuration
public class SaSsoAutoConfigure implements InitializingBean {
    @Inject
    private AppContext appContext;

    @Override
    public void afterInjection() throws Throwable {
        appContext.subBeansOfType(SaSsoTemplate.class, bean->{
            SaSsoUtil.ssoTemplate = bean;
            SaSsoProcessor.instance.ssoTemplate = bean;
        });

        appContext.subBeansOfType(SaSsoConfig.class, bean->{
            SaSsoManager.setConfig(bean);
        });
    }

    /**
     * 获取 SSO 配置Bean
     * */
    @Bean
    public SaSsoConfig getConfig(@Inject(value = "${sa-token.sso}",required = false) SaSsoConfig ssoConfig) {
        return ssoConfig;
    }
}
