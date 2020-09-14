package org.noear.solon.extend.validation;

import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;
import org.noear.solon.extend.validation.annotation.*;

/**
 *
 * @author noear
 * @since 1.0
 * */
public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        Aop.getAsyn(ValidatorFailureHandler.class, (bw) -> {
            ValidatorManager.global().setFailureHandler(bw.raw());
        });

        Aop.getAsyn(NoRepeatLock.class, (bw) -> {
            ValidatorManager.setNoRepeatLock(bw.raw());
        });

        Aop.getAsyn(WhitelistChecker.class, (bw) -> {
            ValidatorManager.setWhitelistChecker(bw.raw());
        });


        Aop.getAsyn(NoRepeatSubmitValidator.class, (bw) -> {
            ValidatorManager.global().register(NoRepeatSubmit.class, bw.raw());
        });

        Aop.getAsyn(WhitelistValidator.class, (bw) -> {
            ValidatorManager.global().register(Whitelist.class, bw.raw());
        });
    }
}
