package org.noear.solon.validation;

import org.noear.solon.Solon;
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
        ValidatorManager.VALIDATE_ALL = Solon.cfg().getBool("solon.validation.validateAll", false);

        //ValidatorFailureHandler
        context.getBeanAsync(ValidatorFailureHandler.class, (bean) -> {
            ValidatorManager.setFailureHandler(bean);
        });

        //NoRepeatSubmitChecker
        context.getBeanAsync(NoRepeatSubmitChecker.class, (bean) -> {
            ValidatorManager.setNoRepeatSubmitChecker(bean);
        });

        //LoginedChecker
        context.getBeanAsync(LoginedChecker.class, (bean) -> {
            ValidatorManager.setLoginedChecker(bean);
        });

        //WhitelistChecker
        context.getBeanAsync(WhitelistChecker.class, (bean) -> {
            ValidatorManager.setWhitelistChecker(bean);
        });

        //NotBlacklistChecker
        context.getBeanAsync(NotBlacklistChecker.class, (bean) -> {
            ValidatorManager.setNotBlacklistChecker(bean);
        });
    }
}
