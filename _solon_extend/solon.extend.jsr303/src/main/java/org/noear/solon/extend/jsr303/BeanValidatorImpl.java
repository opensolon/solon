package org.noear.solon.extend.jsr303;

import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.jsr303.utils.BeanValidationUtils;
import org.noear.solon.validation.BeanValidator;

import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * 有需要，手动注册到容器
 *
 * @author noear
 * @since 1.3
 */
public class BeanValidatorImpl implements BeanValidator {
    @Override
    public Result validate(Object object, Class<?>... groups) {
        Set<ConstraintViolation<Object>> violationSet = BeanValidationUtils.validate(object, groups);

        if (violationSet.isEmpty()) {
            return Result.succeed();
        } else {
            ConstraintViolation<Object> violation = violationSet.iterator().next();
            return Result.failure(violation.getMessage());
        }
    }
}
