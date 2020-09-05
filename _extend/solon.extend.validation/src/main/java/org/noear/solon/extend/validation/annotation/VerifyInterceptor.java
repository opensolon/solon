package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.XAction;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;


public class VerifyInterceptor implements XHandler {
    List<ValidatorEntity> validators = new ArrayList<>();

    public VerifyInterceptor() {
        initialize();
    }

    protected void initialize() {
//        add(NoRepeatSubmit.class, new NoRepeatSubmitValidator());
//        add(NotNull.class, new NotNullValidator());
//        add(NotEmpty.class, new NotEmptyValidator());
//        add(NotZero.class, new NotZeroValidator());
//        add(Whitelist.class, new WhitelistChecker());
    }

    protected <T extends Annotation> void add(Class<T> type, Validator<T> validator) {
        validators.add(new ValidatorEntity(type, validator));
    }

    protected <T extends Annotation> void addAt(int index, Class<T> type, Validator<T> validator) {
        if (validators.size() >= index) {
            validators.add(index, new ValidatorEntity(type, validator));
        } else {
            validators.add(new ValidatorEntity(type, validator));
        }
    }

    @Override
    public void handle(XContext ctx) throws Throwable {
        XAction action = ctx.attr("action");

        if (action != null) {
            handle0(ctx, action);
        }
    }

    protected void handle0(XContext ctx, XAction action) throws Throwable {
        StringBuilder sb = new StringBuilder();

        for (ValidatorEntity m1 : validators) {
            if (ctx.getHandled()) {
                return;
            }

            Annotation anno = action.method().getAnnotation(m1.type);
            if (anno != null) {
                m1.validator.verify(ctx, action, anno, sb);
            }
        }
    }
}
