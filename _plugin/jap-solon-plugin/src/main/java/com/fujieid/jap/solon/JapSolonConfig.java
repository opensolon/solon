package com.fujieid.jap.solon;

import com.fujieid.jap.core.JapUserService;
import com.fujieid.jap.core.config.JapConfig;
import com.fujieid.jap.simple.SimpleStrategy;
import com.fujieid.jap.social.SocialStrategy;
import com.fujieid.jap.sso.JapMfa;
import com.fujieid.jap.sso.JapMfaService;
import com.fujieid.jap.sso.config.JapSsoConfig;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.Aop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JapSolonConfig {
    private static final Logger log = LoggerFactory.getLogger(JapSolonConfig.class);

    @Inject
    JapProps japProps;
    @Inject
    private JapUserService japUserService;
    @Inject
    private JapMfaService japMfaService;

    private SocialStrategy socialStrategy;
    private SimpleStrategy simpleStrategy;
    private JapConfig japConfig;

    /**
     * 初始化
     */
    @Init
    private void initialize() {
        log.info("SSO：{}", this.japProps.isSso() ? "已启用" : "未启用");
        log.info("Cookie Domain：{}", this.japProps.getDomain());

        this.japConfig = new JapConfig()
                .setSso(this.japProps.isSso())
                .setSsoConfig(new JapSsoConfig()
                        .setCookieDomain(this.japProps.getDomain()));

        this.socialStrategy = new SocialStrategy(this.japUserService, this.japConfig);
        this.simpleStrategy = new SimpleStrategy(this.japUserService, this.japConfig);

        if(this.japMfaService != null) {
            JapMfa japMfa = new JapMfa(this.japMfaService);
            Aop.wrapAndPut(JapMfa.class, japMfa);
        }

        log.info("Mfa：{}", this.japMfaService != null ? "已启用" : "未启用");
    }

    public SocialStrategy getSocialStrategy() {
        return this.socialStrategy;
    }

    public SimpleStrategy getSimpleStrategy() {
        return this.simpleStrategy;
    }

    public JapConfig getJapConfig() {
        return this.japConfig;
    }

}
