package cn.dev33.satoken.solon.oauth2;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2Config;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Template;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Util;
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
@Condition(onClass = SaOAuth2Manager.class)
@Configuration
public class SaOAuth2AutoConfigure implements InitializingBean {
    @Inject
    private AppContext appContext;

    @Override
    public void afterInjection() throws Throwable {
        appContext.subBeansOfType(SaOAuth2Template.class, bean -> {
            SaOAuth2Util.saOAuth2Template = bean;
        });

        appContext.subBeansOfType(SaOAuth2Config.class, bean -> {
            SaOAuth2Manager.setConfig(bean);
        });
    }

    /**
     * 获取 OAuth2配置Bean
     */
    @Bean
    public SaOAuth2Config getConfig(@Inject(value = "${sa-token.oauth2}", required = false) SaOAuth2Config oAuth2Config) {
        return oAuth2Config;
    }
}
