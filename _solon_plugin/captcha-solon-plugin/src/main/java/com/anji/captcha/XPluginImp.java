package com.anji.captcha;

import com.anji.captcha.config.AjCaptchaServiceConfiguration;
import com.anji.captcha.config.AjCaptchaStorageConfiguration;
import com.anji.captcha.properties.AjCaptchaProperties;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import com.anji.captcha.controller.CaptchaController;

/**
 * @author noear
 * @since 1.5
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        context.lifecycle(-99, () -> {
            context.beanMake(AjCaptchaProperties.class);
            context.beanMake(AjCaptchaServiceConfiguration.class);
            context.beanMake(AjCaptchaStorageConfiguration.class);
            context.beanMake(CaptchaController.class);
        });
    }
}
