package org.noear.solon.validation;

import org.noear.solon.core.handle.Result;

/**
 * @author noear
 * @since 1.5
 */
public class BeanValidatorImpl implements BeanValidator {
    @Override
    public Result validate(Object obj, Class<?>... groups) {
        return ValidatorManager.validateOfEntity(obj);
    }
}
