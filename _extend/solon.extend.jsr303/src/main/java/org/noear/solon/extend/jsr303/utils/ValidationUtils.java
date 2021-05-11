package org.noear.solon.extend.jsr303.utils;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.hibernate.validator.HibernateValidator;
import org.noear.solon.core.handle.Result;

import java.util.Set;

/**
 * Bean Validation Utils
 *
 * @author noear
 * @since 1.3
 */
public class ValidationUtils {
    /**
     * 验证器
     */
    private static Validator validator;

    static {
        validator = Validation.byProvider(HibernateValidator.class)
                .configure()
                .failFast(true)
                .buildValidatorFactory()
                .getValidator();
    }

    /**
     * 验证方法
     *
     * @param object 被校验的对象
     * @param groups 被校验的组
     */
    public static Result validate(Object object, Class<?>... groups) {
        Set<ConstraintViolation<Object>> violationSet = validator.validate(object, groups);

        if (violationSet.isEmpty()) {
            return Result.succeed();
        } else {
            ConstraintViolation<Object> violation = violationSet.iterator().next();
            return Result.failure(violation.getMessage());
        }
    }
}