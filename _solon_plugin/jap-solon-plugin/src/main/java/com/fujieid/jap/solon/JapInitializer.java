package com.fujieid.jap.solon;

import com.fujieid.jap.core.JapUserService;
import com.fujieid.jap.simple.SimpleStrategy;
import com.fujieid.jap.social.SocialStrategy;
import com.fujieid.jap.solon.http.controller.AccountController;
import com.fujieid.jap.solon.http.controller.MfaController;
import com.fujieid.jap.solon.http.controller.SimpleController;
import com.fujieid.jap.solon.http.controller.SocialController;
import com.fujieid.jap.sso.JapMfa;
import com.fujieid.jap.sso.JapMfaService;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.AopContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 颖
 * @author work
 * @since 1.6
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

    @Inject
    AopContext context;

    /**
     * 初始化
     */
    @Init
    private void initialize() {
        log.info("Just Auth Plus: ");
        log.info("\tSimple: {}", japProperties.getSimpleConfig() != null ? "已启用" : "未启用");
        log.info("\tSocial: {}", japProperties.getCredentials() != null ? "已启用" : "未启用");
        log.info("\tMfa: {}", japMfaService != null ? "已启用" : "未启用");

        // 启用用户接口
        Solon.app().add(japProperties.getAccountPath(), AccountController.class);

        // 启用 Simple
        if(japProperties.getSimpleConfig() != null) {
            context.wrapAndPut(SimpleStrategy.class, new SimpleStrategy(japUserService, japProperties.getJapConfig()));
            Solon.app().add(japProperties.getAuthPath(), SimpleController.class);
        }

        // 启用 Social
        if(japProperties.getCredentials() != null) {
            context.wrapAndPut(SocialStrategy.class, new FixedSocialStrategy(japUserService, japProperties.getJapConfig()));
            Solon.app().add(japProperties.getAuthPath(), SocialController.class);
        }

        // 启用 Mfa
        if(japMfaService != null) {
            JapMfa japMfa = new JapMfa(japMfaService);
            context.wrapAndPut(JapMfa.class, japMfa);
            Solon.app().add(japProperties.getAuthPath(), MfaController.class);
        }
    }
}
