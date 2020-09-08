package org.noear.solon.extend.validation;

import org.noear.solon.XUtil;
import org.noear.solon.annotation.XNote;
import org.noear.solon.core.*;
import org.noear.solon.extend.validation.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证管理器
 *
 * 只支持XController 和 XAction
 *
 * @author noear
 * @since 1.0.22
 * */
public class ValidatorManager implements XHandler {
    private static ValidatorManager global = new ValidatorManager();

    public static ValidatorManager global() {
        return global;
    }

    public static void globalSet(ValidatorManager global) {
        if (global != null) {
            ValidatorManager.global = global;
        }
    }

    public static void setNoRepeatLock(NoRepeatLock lock){
        NoRepeatLockImp.globalSet(lock);
    }

    public static void setWhitelistChecker(WhitelistChecker checker){
        WhitelistCheckerImp.globalSet(checker);
    }


    protected final Map<Class<? extends Annotation>, Validator> validMap = new HashMap<>();
    protected final ValidatorEventHandler handler;

    public ValidatorManager() {
        handler = new ValidatorEventHandlerImp();
        initialize();
    }

    public ValidatorManager(ValidatorEventHandler handler) {
        if (handler == null) {
            this.handler = new ValidatorEventHandlerImp();
        } else {
            this.handler = handler;
        }

        initialize();
    }

    protected void initialize() {
        register(Date.class, DateValidator.instance);

        register(DecimalMax.class, DecimalMaxValidator.instance);
        register(DecimalMin.class, DecimalMinValidator.instance);

        register(Email.class, EmailValidator.instance);

        register(Max.class, MaxValidator.instance);
        register(Min.class, MinValidator.instance);

        register(NoRepeatSubmit.class, NoRepeatSubmitValidator.instance);

        register(NotBlank.class, NotBlankValidator.instance);
        register(NotEmpty.class, NotEmptyValidator.instance);
        register(NotNull.class, NotNullValidator.instance);
        register(NotZero.class, NotZeroValidator.instance);

        register(Null.class, NullValidator.instance);

        register(Pattern.class, PatternValidator.instance);
        register(Length.class, LengthValidator.instance);
        register(Whitelist.class, WhitelistValidator.instance);
    }

    /**
     * 清除所有验证器
     * */
    @XNote("清除所有验证器")
    public void clear() {
        validMap.clear();
    }

    /**
     * 移除某个类型的验证器
     * */
    @XNote("移除某个类型的验证器")
    public <T extends Annotation> void remove(Class<T> type){
        validMap.remove(type);
    }

    /**
     * 注册验证器
     * */
    @XNote("注册验证器")
    public <T extends Annotation> void register(Class<T> type, Validator<T> validator) {
        validMap.put(type, validator);
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

        for (Annotation anno : action.bean().getAnnotations()) {
            if (validateDo(ctx, anno, null, tmp)) {
                return;
            }
        }

        for (Annotation anno : action.method().getAnnotations()) {
            if (validateDo(ctx, anno, null, tmp)) {
                return;
            }
        }

        for (Parameter para : action.method().getParameters()) {
            for (Annotation anno : para.getAnnotations()) {
                if (validateDo(ctx, anno, para.getName(), tmp)) {
                    return;
                }
            }
        }
    }

    protected boolean validateDo(XContext ctx, Annotation anno, String name, StringBuilder tmp){
        if (ctx.getHandled()) {
            return true;
        }

        Validator valid = validMap.get(anno.annotationType());

        if (valid != null) {
            tmp.setLength(0);
            XResult rst = valid.validate(ctx, anno, name, tmp);

            if (rst.getCode() != 1) {
                if (this.failureDo(ctx, anno, rst, valid.message(anno))) {
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean failureDo(XContext ctx, Annotation ano, XResult result, String message){
        return handler.onFailure(ctx,ano,result,message);
    }

    static class ValidatorEventHandlerImp implements ValidatorEventHandler {

        @Override
        public boolean onFailure(XContext ctx, Annotation ano, XResult result, String message) {
            ctx.setHandled(true);
            ctx.statusSet(400);
            try {
                if (XUtil.isEmpty(message)) {
                    message = ano.annotationType().getSimpleName() + " verification failed: " + result.getDescription();
                }

                ctx.render(XResult.failure(400, message));
            } catch (Throwable ex) {
                XUtil.throwTr(ex);
            }

            return true;
        }
    }
}
