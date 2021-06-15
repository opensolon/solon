package org.noear.solon.validation;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.validation.annotation.*;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证管理器
 *
 * 只支持 Controller 和 Action
 *
 * @author noear
 * @since 1.0
 * */
public class ValidatorManager {

    /**
     * 设定非重复提交检测器
     * */
    public static void setNoRepeatSubmitChecker(NoRepeatSubmitChecker checker) {
        NoRepeatSubmitValidator.instance.setChecker(checker);
    }

    /**
     * 设定已登录状态检测器
     * */
    public static void setLoginedChecker(LoginedChecker checker) {
        LoginedValidator.instance.setChecker(checker);
    }

    /**
     * 设定白名单检测器
     * */
    public static void setWhitelistChecker(WhitelistChecker checker) {
        WhitelistValidator.instance.setChecker(checker);
    }

    /**
     * 设定非黑名单检测器
     * */
    public static void setNotBlacklistChecker(NotBlacklistChecker checker) {
        NotBlacklistValidator.instance.setChecker(checker);
    }

    /**
     * 设定错误处理
     * */
    public static void setFailureHandler(ValidatorFailureHandler handler) {
        if (handler != null) {
            failureHandler = handler;
        }
    }


    private static final Map<Class<? extends Annotation>, Validator> validMap = new HashMap<>();
    private static ValidatorFailureHandler failureHandler = new ValidatorFailureHandlerImp();

    static {
        initialize();
    }

    private static void initialize() {
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
        register(Numeric.class, NumericValidator.instance);

        register(Pattern.class, PatternValidator.instance);
        register(Length.class, LengthValidator.instance);
        register(Whitelist.class, WhitelistValidator.instance);
        register(Logined.class, LoginedValidator.instance);

        register(NotBlacklist.class, NotBlacklistValidator.instance);
    }

    /**
     * 清除所有验证器
     */
    @Note("清除所有验证器")
    public static void clear() {
        validMap.clear();
    }

    /**
     * 移除某个类型的验证器
     */
    @Note("移除某个类型的验证器")
    public static <T extends Annotation> void remove(Class<T> type) {
        validMap.remove(type);
    }

    /**
     * 注册验证器
     */
    @Note("注册验证器")
    public static <T extends Annotation> void register(Class<T> type, Validator<T> validator) {
        validMap.put(type, validator);
    }

    /**
     * 执行验证处理
     * */
    @Note("执行验证处理")
    public static void validateDo(Context ctx, Action action) throws Throwable {
        StringBuilder tmp = new StringBuilder();

        for (Annotation anno : action.bean().annotations()) {
            if (validateDo0(ctx, anno, null, tmp)) {
                return;
            }
        }

        for (Annotation anno : action.method().getAnnotations()) {
            if (validateDo0(ctx, anno, null, tmp)) {
                return;
            }
        }

        for (ParamWrap para : action.method().getParamWraps()) {
            for (Annotation anno : para.getParameter().getAnnotations()) {
                if (validateDo0(ctx, anno, para.getName(), tmp)) {
                    return;
                }
            }
        }
    }

    private static boolean validateDo0(Context ctx, Annotation anno, String name, StringBuilder tmp) {
        if (ctx.getHandled()) {
            return true;
        }

        Validator valid = validMap.get(anno.annotationType());

        if (valid != null) {
            tmp.setLength(0);
            Result rst = valid.validateOfContext(ctx, anno, name, tmp);

            if (rst.getCode() != Result.SUCCEED_CODE) {
                if (failureDo(ctx, anno, rst, valid.message(anno))) {
                    return true;
                }
            }
        }

        return false;
    }



    /**
     * 执行错误处理
     * */
    @Note("执行错误处理")
    public static boolean failureDo(Context ctx, Annotation ano, Result result, String message) {
        if (ctx == null) {
            return false;
        }

        try {
            return failureHandler.onFailure(ctx, ano, result, message);
        } catch (Throwable ex) {
            ex = Utils.throwableUnwrap(ex);
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            } else {
                throw new RuntimeException(ex);
            }
        }
    }
}
