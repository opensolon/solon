package org.noear.solon.extend.vaptcha;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.vaptcha.http.request.validators.Vaptcha;
import org.noear.solon.extend.vaptcha.http.request.validators.VaptchaValidator;
import org.noear.solon.validation.ValidatorManager;

/**
 * @author iYarnFog
 * @since 1.5
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        ValidatorManager.register(Vaptcha.class, new VaptchaValidator());
    }
}
