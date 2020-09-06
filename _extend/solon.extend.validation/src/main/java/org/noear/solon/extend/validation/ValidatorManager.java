package org.noear.solon.extend.validation;

import org.noear.solon.XUtil;
import org.noear.solon.core.*;
import org.noear.solon.extend.validation.annotation.*;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证管理器
 *
 * 只支持XController
 *
 * @author noear
 * @since 1.0.22
 * */
public class ValidatorManager implements XHandler {
    public static XHandler instance = new ValidatorManager();

    protected Map<Class<? extends Annotation>, Validator> validMap = new HashMap<>();

    public ValidatorManager() {
        initialize();
    }

    protected void initialize() {
        add(DecimalMax.class, DecimalMaxValidator.instance);
        add(DecimalMin.class, DecimalMinValidator.instance);

        add(Max.class, MaxValidator.instance);
        add(Min.class, MinValidator.instance);

        add(NoRepeatSubmit.class, NoRepeatSubmitValidator.instance);

        add(NotBlank.class, NotBlankValidator.instance);
        add(NotEmpty.class, NotEmptyValidator.instance);
        add(NotNull.class, NotNullValidator.instance);
        add(NotZero.class, NotZeroValidator.instance);

        add(Null.class, NullValidator.instance);

        add(Pattern.class, PatternValidator.instance);
        add(Size.class, SizeValidator.instance);
    }

    public void clear() {
        validMap.clear();
    }

    public <T extends Annotation> void add(Class<T> type, Validator<T> validator) {
        validMap.put(type, validator);
    }


    @Override
    public void handle(XContext ctx) throws Throwable {
        XAction action = ctx.action();

        if (action != null) {
            validateDo(ctx, action);
        }
    }

    protected void validateDo(XContext ctx, XAction action) throws Throwable {
        StringBuilder tmp = new StringBuilder();
        XResult rst = null;

        for (Annotation anno : action.method().getMethod().getAnnotations()) {
            if (ctx.getHandled()) {
                return;
            }

            Validator valid = validMap.get(anno.annotationType());
            if (valid != null) {
                tmp.setLength(0);
                rst = valid.validate(ctx, anno, tmp);

                if (rst.getCode() != 1) {
                    if (renderDo(ctx, anno, rst)) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * @return 是否停止后续检查器
     */
    protected boolean renderDo(XContext ctx, Annotation ano, XResult rst) {
        ctx.setHandled(true);
        ctx.statusSet(400);
        try {
            String message = ano.annotationType().getSimpleName() + " verification failed: " + rst.getDescription();
            ctx.render(XResult.failure(400, message));
        } catch (Throwable ex) {
            XUtil.throwTr(ex);
        }

        return true;
    }
}
