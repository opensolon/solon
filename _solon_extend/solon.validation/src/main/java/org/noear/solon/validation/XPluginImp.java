package org.noear.solon.validation;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.validation.annotation.LoginedChecker;
import org.noear.solon.validation.annotation.NoRepeatSubmitChecker;
import org.noear.solon.validation.annotation.NotBlacklistChecker;
import org.noear.solon.validation.annotation.WhitelistChecker;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {

        //ValidatorFailureHandler
        context.getWrapAsyn(ValidatorFailureHandler.class, (bw) -> {
            ValidatorManager.setFailureHandler(bw.raw());
        });

        //NoRepeatSubmitChecker
        context.getWrapAsyn(NoRepeatSubmitChecker.class, (bw) -> {
            ValidatorManager.setNoRepeatSubmitChecker(bw.raw());
        });

        //LoginedChecker
        context.getWrapAsyn(LoginedChecker.class, (bw) -> {
            ValidatorManager.setLoginedChecker(bw.raw());
        });

        //WhitelistChecker
        context.getWrapAsyn(WhitelistChecker.class, (bw) -> {
            ValidatorManager.setWhitelistChecker(bw.raw());
        });

        //NotBlacklistChecker
        context.getWrapAsyn(NotBlacklistChecker.class, (bw) -> {
            ValidatorManager.setNotBlacklistChecker(bw.raw());
        });
    }
}
