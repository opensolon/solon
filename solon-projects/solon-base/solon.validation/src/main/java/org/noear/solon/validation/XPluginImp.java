package org.noear.solon.validation;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.validation.annotation.*;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        ValidatorManager.VALIDATE_ALL = Solon.cfg().getBool("solon.validation.validateAll", false);

        boolean allControllerValidation = Solon.cfg().getBool("solon.validation.allControllerValidation", false);

        if(allControllerValidation){
            // 给所有controller开启效验
            context.beanInterceptorAdd(Controller.class, new BeanValidateInterceptor(), 1);
        }else{
            context.beanInterceptorAdd(Valid.class, new BeanValidateInterceptor(), 1);
        }

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
