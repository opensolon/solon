package org.noear.solon.extend.validation;

import org.noear.solon.core.XAction;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.annotation.*;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class ValidatorManager {
    public static ValidatorManager instance = new ValidatorManager();

    protected List<ValidatorEntity> validators = new ArrayList<>();

    public ValidatorManager() {
        initialize();
    }

    protected void initialize() {
        add(NoRepeatSubmit.class,  new NoRepeatSubmitValidator());
        add(NotNull.class,  new NotNullValidator());
        add(NotEmpty.class,  new NotEmptyValidator());
        add(NotBlank.class,  new NotBlankValidator());
        add(NotZero.class,  new NotZeroValidator());
    }

    protected <T extends Annotation> void add(Class<T> type, Validator<T> validator) {
        for(ValidatorEntity ve : validators){
            if(ve.type == type){
                return;
            }
        }

        validators.add(new ValidatorEntity(type, validator));
    }

    protected <T extends Annotation> void addAt(int index, Class<T> type,Validator<T> validator) {
        for(ValidatorEntity ve : validators){
            if(ve.type == type){
                return;
            }
        }

        if (validators.size() >= index) {
            validators.add(index, new ValidatorEntity(type, validator));
        } else {
            validators.add(new ValidatorEntity(type, validator));
        }
    }

    public void validate(XContext ctx, XAction action) throws Throwable {
        StringBuilder tmp = new StringBuilder();
        XResult rst = null;

        for (ValidatorEntity m1 : validators) {
            if (ctx.getHandled()) {
                return;
            }

            Annotation anno = action.method().getAnnotation(m1.type);
            if (anno != null) {
                tmp.setLength(0);
                rst = m1.validator.validate(ctx, anno, tmp);

                if (rst.getCode() != 1) {
                    if (notVerified(ctx, rst)) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * @return 是否停止后续检查器
     * */
    protected boolean notVerified(XContext ctx, XResult rst) {
        ctx.setHandled(true);
        ctx.statusSet(400);
        rst.setCode(400);

        return true;
    }
}
