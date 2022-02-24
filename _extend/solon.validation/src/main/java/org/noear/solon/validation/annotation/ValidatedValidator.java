package org.noear.solon.validation.annotation;

import org.noear.solon.core.Aop;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.BeanValidator;
import org.noear.solon.validation.BeanValidatorDefault;
import org.noear.solon.validation.Validator;

/**
 * @author noear
 * @since 1.5
 */
public class ValidatedValidator implements Validator<Validated> {
    public static final ValidatedValidator instance = new ValidatedValidator();

    private BeanValidator validator;

    public ValidatedValidator() {
        validator = new BeanValidatorDefault();

        Aop.getAsyn(BeanValidator.class, bw -> {
            validator = bw.get();
        });
    }

    @Override
    public Result validateOfValue(String label, Validated anno, Object val, StringBuilder tmp) {
        return validator.validate(val, anno.value());
    }

    @Override
    public Result validateOfContext(Context ctx, Validated anno, String name, StringBuilder tmp) {
        return null;
    }
}
