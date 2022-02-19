package org.noear.solon.plugin.jap.properties;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;
import com.fujieid.jap.core.JapUserService;
import com.fujieid.jap.core.config.JapConfig;
import com.fujieid.jap.simple.SimpleStrategy;
import com.fujieid.jap.social.SocialStrategy;
import com.fujieid.jap.sso.config.JapSsoConfig;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

@Configuration
public class JapSoloConfig {

    private static final Log log = LogFactory.get();

    @Inject
    private JapProperties japProperties;

    @Inject
    private JapUserService japUserService;

    private SocialStrategy socialStrategy;

    private SimpleStrategy simpleStrategy;

    private JapConfig japConfig;

    public void initStrategy(){
        log.info("初始化JAP配置：{}", JSON.toJSONString(japProperties));
        log.info("是否开启SSO：{}", japProperties.getSso());
        log.info("CookieDomain：{}", japProperties.getDomain());
        japConfig = new JapConfig()
                .setSso(japProperties.getSso())
                .setSsoConfig(new JapSsoConfig()
                        .setCookieDomain(japProperties.getDomain()));
        socialStrategy = new SocialStrategy(japUserService, japConfig);
        simpleStrategy = new SimpleStrategy(japUserService, japConfig);

    }


    public SocialStrategy getSocialStrategy() {
        return socialStrategy;
    }

    public SimpleStrategy getSimpleStrategy() {
        return simpleStrategy;
    }

    public JapConfig getJapConfig() {
        return japConfig;
    }
}
