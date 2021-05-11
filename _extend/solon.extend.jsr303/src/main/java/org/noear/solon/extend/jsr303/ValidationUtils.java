package org.noear.solon.extend.jsr303;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.hibernate.validator.HibernateValidator;

import java.util.Set;

/**
 * @author noear
 * @since 1.3
 */
public class ValidationUtils {
    /**
     * 验证器
     */
    private static Validator validator;

    static {
        //validator = Validation.buildDefaultValidatorFactory().getValidator();
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
    public static void validate(Object object, Class<?>... groups) {
        // 用验证器执行验证，返回一个违反约束的set集合
        Set<ConstraintViolation<Object>> violationSet = validator.validate(object, groups);
        // 判断是否为空，空：说明验证通过，否则就验证失败
        if (!violationSet.isEmpty()) {
            // 获取第一个验证失败的属性
            ConstraintViolation<Object> violation = violationSet.iterator().next();
            // 抛出自定义异常
            throw new RuntimeException(violation.getMessage());
        }
    }
}