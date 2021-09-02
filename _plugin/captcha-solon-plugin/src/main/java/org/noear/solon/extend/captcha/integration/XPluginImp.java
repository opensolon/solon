package org.noear.solon.extend.captcha.integration;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.captcha.CaptchaController;

/**
 * @author noear
 * @since 1.5
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        app.beanMake(AjCaptchaProperties.class);
        app.beanMake(AjCaptchaServiceConfiguration.class);
        app.beanMake(AjCaptchaStorageConfiguration.class);
        app.beanMake(CaptchaController.class);
    }
}
