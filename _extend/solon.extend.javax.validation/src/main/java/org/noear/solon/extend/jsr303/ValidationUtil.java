package org.noear.solon.extend.jsr303;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * @author noear
 * @since 1.3
 */
public class ValidationUtil {
    private static ValidatorFactory factory;
    public static Validator validator;

    public static void init() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public static void clean() {
        factory.close();
    }

}