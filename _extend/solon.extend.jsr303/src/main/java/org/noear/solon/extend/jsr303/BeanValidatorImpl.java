package org.noear.solon.extend.jsr303;

import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.jsr303.utils.ValidationUtils;
import org.noear.solon.extend.validation.BeanValidator;

/**
 * @author noear
 * @since 1.3
 */
public class BeanValidatorImpl implements BeanValidator {
    @Override
    public Result validate(Object object, Class<?>... groups) {
        return ValidationUtils.validate(object, groups);
    }
}
