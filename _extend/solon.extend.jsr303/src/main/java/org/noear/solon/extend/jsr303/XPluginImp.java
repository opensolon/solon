package org.noear.solon.extend.jsr303;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.validation.BeanValidator;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        Aop.wrapAndPut(BeanValidator.class, new BeanValidatorImpl());
    }
}
