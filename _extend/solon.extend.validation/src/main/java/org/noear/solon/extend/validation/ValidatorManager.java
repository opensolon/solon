package org.noear.solon.extend.validation;

import org.noear.solon.XUtil;
import org.noear.solon.core.*;
import org.noear.solon.extend.validation.annotation.*;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class ValidatorManager implements XHandler {
    public static XHandler instance = new ValidatorManager();

    protected List<ValidatorEntity> validators = new ArrayList<>();
    protected XRender render;

    public void setRender(XRender render) {
        if (render != null) {
            this.render = render;
        }
    }

    public ValidatorManager() {
        initialize();
    }

    protected void initialize() {
        add(NoRepeatSubmit.class, NoRepeatSubmitValidator.instance);
        add(NotNull.class, NotNullValidator.instance);
        add(NotEmpty.class, NotEmptyValidator.instance);
        add(NotBlank.class, NotBlankValidator.instance);
        add(NotZero.class, NotZeroValidator.instance);
        add(Min.class, MinValidator.instance);
        add(Max.class, MaxValidator.instance);
    }

    public void clear() {
        validators.clear();
    }

    public <T extends Annotation> void add(Class<T> type, Validator<T> validator) {
        for (ValidatorEntity ve : validators) {
            if (ve.type == type) {
                return;
            }
        }

        validators.add(new ValidatorEntity(type, validator));
    }

    public <T extends Annotation> void addAt(int index, Class<T> type, Validator<T> validator) {
        for (ValidatorEntity ve : validators) {
            if (ve.type == type) {
                return;
            }
        }

        if (validators.size() >= index) {
            validators.add(index, new ValidatorEntity(type, validator));
        } else {
            validators.add(new ValidatorEntity(type, validator));
        }
    }


    @Override
    public void handle(XContext ctx) throws Throwable {
        XAction action = ctx.action();

        if (action != null) {
            validate(ctx, action);
        }
    }

    protected void validate(XContext ctx, XAction action) throws Throwable {
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
                    if (notVerified(ctx, anno, rst)) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * @return 是否停止后续检查器
     */
    protected boolean notVerified(XContext ctx, Annotation anno, XResult rst) {
        ctx.setHandled(true);
        ctx.statusSet(400);
        try {
            String message = anno.annotationType().getSimpleName() + " verification failed: " + rst.getDescription();
            ctx.render(XResult.failure(400, message));
        } catch (Throwable ex) {
            XUtil.throwTr(ex);
        }

        return true;
    }
}
