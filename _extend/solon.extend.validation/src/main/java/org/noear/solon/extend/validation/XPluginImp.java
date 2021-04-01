package org.noear.solon.extend.validation;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.validation.annotation.LoginedChecker;
import org.noear.solon.extend.validation.annotation.NotBlacklistChecker;
import org.noear.solon.extend.validation.annotation.WhitelistChecker;

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


        //LoginedChecker
        Aop.getAsyn(LoginedChecker.class, (bw) -> {
            ValidatorManager.setLoginedChecker(bw.raw());
        });

        //WhitelistChecker
        Aop.getAsyn(WhitelistChecker.class, (bw) -> {
            ValidatorManager.setWhitelistChecker(bw.raw());
        });

        //NotBlacklistChecker
        Aop.getAsyn(NotBlacklistChecker.class, (bw) -> {
            ValidatorManager.setNotBlacklistChecker(bw.raw());
        });
    }
}
