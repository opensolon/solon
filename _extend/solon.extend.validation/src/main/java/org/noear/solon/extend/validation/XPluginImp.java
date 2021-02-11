package org.noear.solon.extend.validation;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        Aop.getAsyn(ValidatorFailureHandler.class, (bw) -> {
            ValidatorManager.global().onFailure(bw.raw());
        });
    }
}
