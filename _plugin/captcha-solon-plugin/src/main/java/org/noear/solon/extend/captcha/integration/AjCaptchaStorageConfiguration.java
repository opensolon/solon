package org.noear.solon.extend.captcha.integration;

import com.anji.captcha.service.CaptchaCacheService;
import com.anji.captcha.service.impl.CaptchaServiceFactory;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

/**
 * @author noear
 * @since 1.5
 */
@Configuration
public class AjCaptchaStorageConfiguration {
    public AjCaptchaStorageConfiguration() {
    }

    @Bean("AjCaptchaCacheService")
    public CaptchaCacheService captchaCacheService(AjCaptchaProperties ajCaptchaProperties) {
        return CaptchaServiceFactory.getCache(ajCaptchaProperties.getCacheType().name());
    }
}
