package com.fujieid.jap.solon;

import com.fujieid.jap.core.JapUserService;
import com.fujieid.jap.simple.SimpleStrategy;
import com.fujieid.jap.social.SocialStrategy;
import com.fujieid.jap.solon.http.controller.SimpleController;
import com.fujieid.jap.solon.http.controller.SocialController;
import com.fujieid.jap.sso.JapMfa;
import com.fujieid.jap.sso.JapMfaService;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.Aop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 颖
 * @author work
 */
@Component
public class JapInitializer {

    private static final Logger log = LoggerFactory.getLogger(JapInitializer.class);

    @Inject
    JapProps japProperties;
    @Inject
    JapUserService japUserService;
    @Inject
    JapMfaService japMfaService;

    /**
     * 初始化
     */
    @Init
    private void initialize() {
        log.info("Just Auth Plus: ");
        log.info("\tSimple: {}", this.japProperties.getSimpleConfig() != null ? "已启用" : "未启用");
        log.info("\tSocial: {}", this.japProperties.getCredentials() != null ? "已启用" : "未启用");
        log.info("\tMfa: {}", this.japMfaService != null ? "已启用" : "未启用");
        // 启用 Simple
        if(this.japProperties.getSimpleConfig() != null) {
            Aop.wrapAndPut(SimpleStrategy.class, new SimpleStrategy(this.japUserService, this.japProperties.getJapConfig()));
            Solon.global().add(this.japProperties.getBasePath(), SimpleController.class);
        }
        // 启用 Social
        if(this.japProperties.getCredentials() != null) {
            Aop.wrapAndPut(SocialStrategy.class, new SocialStrategy(this.japUserService, this.japProperties.getJapConfig()));
            Solon.global().add(this.japProperties.getBasePath(), SocialController.class);
        }
        // 启用 Mfa
        if(this.japMfaService != null) {
            JapMfa japMfa = new JapMfa(this.japMfaService);
            Aop.wrapAndPut(JapMfa.class, japMfa);
        }
    }

}
