package com.anji.captcha;

import com.anji.captcha.config.AjCaptchaServiceConfiguration;
import com.anji.captcha.config.AjCaptchaStorageConfiguration;
import com.anji.captcha.properties.AjCaptchaProperties;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import com.anji.captcha.controller.CaptchaController;

/**
 * @author noear
 * @since 1.5
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        app.beanMake(AjCaptchaProperties.class);
        app.beanMake(AjCaptchaServiceConfiguration.class);
        app.beanMake(AjCaptchaStorageConfiguration.class);
        app.beanMake(CaptchaController.class);
    }
}
