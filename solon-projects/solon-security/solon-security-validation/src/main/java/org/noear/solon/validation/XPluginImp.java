/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.validation;

import org.noear.solon.Solon;
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

        context.beanInterceptorAdd(Valid.class, new BeanValidateInterceptor(), 1);

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
