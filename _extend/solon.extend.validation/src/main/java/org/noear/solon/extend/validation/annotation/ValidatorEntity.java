package org.noear.solon.extend.validation.annotation;

import java.lang.annotation.Annotation;

public class ValidatorEntity<T extends Annotation> {
    Class<T> type;
    Validator<T> validator;

    public ValidatorEntity(Class<T> type, Validator<T> validator) {
        this.type = type;
        this.validator = validator;
    }
}
