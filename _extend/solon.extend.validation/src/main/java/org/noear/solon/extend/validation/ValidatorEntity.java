package org.noear.solon.extend.validation;

import java.lang.annotation.Annotation;

public class ValidatorEntity<T extends Annotation> {
    public Class<T> type;
    public Validator<T> validator;

    public ValidatorEntity(Class<T> type, Validator<T> validator) {
        this.type = type;
        this.validator = validator;
    }
}
