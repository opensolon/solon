package org.noear.solon.validation;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.Aop;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.wrap.FieldWrap;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.ext.DataThrowable;
import org.noear.solon.validation.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
    private static BeanValidator validator;

    static {
        validator = new BeanValidatorDefault();

        Aop.getAsyn(BeanValidator.class, bw -> {
            validator = bw.get();
        });
    }

    /**
     * 设定非重复提交检测器
     */
    public static void setNoRepeatSubmitChecker(NoRepeatSubmitChecker checker) {
        NoRepeatSubmitValidator.instance.setChecker(checker);
    }

    /**
     * 设定已登录状态检测器
     */
    public static void setLoginedChecker(LoginedChecker checker) {
        LoginedValidator.instance.setChecker(checker);
    }

    /**
     * 设定白名单检测器
     */
    public static void setWhitelistChecker(WhitelistChecker checker) {
        WhitelistValidator.instance.setChecker(checker);
    }

    /**
     * 设定非黑名单检测器
     */
    public static void setNotBlacklistChecker(NotBlacklistChecker checker) {
        NotBlacklistValidator.instance.setChecker(checker);
    }

    /**
     * 设定错误处理
     */
    public static void setFailureHandler(ValidatorFailureHandler handler) {
        if (handler != null) {
            failureHandler = handler;
        }
    }


    private static final Map<Class<? extends Annotation>, Validator> validMap = new HashMap<>();
    private static ValidatorFailureHandler failureHandler = new ValidatorFailureHandlerDefault();

    static {
        initialize();
    }

    /**
     * 初始化（验证器注册）
     */
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
        register(Size.class, SizeValidator.instance);

        register(Whitelist.class, WhitelistValidator.instance);
        register(Logined.class, LoginedValidator.instance);

        register(NotBlacklist.class, NotBlacklistValidator.instance);

        register(Validated.class, ValidatedValidator.instance);
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

    @Note("移除某个类型的验证器")
    public static <T extends Annotation> Validator<T> get(Class<T> type) {
        return validMap.get(type);
    }

    /**
     * 执行上下文的验证处理
     */
    @Note("执行上下文的验证处理")
    public static void validateOfContext(Context ctx, Action action) throws Throwable {
        StringBuilder tmp = new StringBuilder();

        for (Annotation anno : action.controller().annotations()) {
            if (validateOfContext0(ctx, anno, null, tmp)) {
                return;
            }
        }

        for (Annotation anno : action.method().getAnnotations()) {
            if (validateOfContext0(ctx, anno, null, tmp)) {
                return;
            }
        }
    }

    private static boolean validateOfContext0(Context ctx, Annotation anno, String name, StringBuilder tmp) {
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

    @Note("执行参数的验证处理")
    public static void validateOfInvocation(Invocation inv) throws Throwable {
        StringBuilder tmp = new StringBuilder();

        for (int i = 0, len = inv.args().length; i < len; i++) {
            ParamWrap pw = inv.method().getParamWraps()[i];

            for (Annotation anno : pw.getParameter().getAnnotations()) {
                //内部会自动抛异常
                validateOfValue0(pw.getName(), anno, inv.args()[i], tmp);
            }
        }
    }

    private static void validateOfValue0(String label, Annotation anno, Object val, StringBuilder tmp) {
        Validator valid = validMap.get(anno.annotationType());

        if (valid != null) {
            tmp.setLength(0);
            Result rst = valid.validateOfValue(anno, val, tmp);

            if (rst.getCode() == Result.FAILURE_CODE) {
                String message = null;

                if (Utils.isEmpty(rst.getDescription())) {
                    rst.setDescription(label);
                }

                if (rst.getData() instanceof BeanValidateInfo) {
                    BeanValidateInfo info = (BeanValidateInfo) rst.getData();
                    anno = info.anno;
                    message = info.message;
                } else {
                    message = valid.message(anno);
                }

                if (ValidatorManager.failureDo(Context.current(), anno, rst, message)) {
                    throw new DataThrowable();
                } else {
                    throw new IllegalArgumentException(rst.getDescription());
                }
            }
        }
    }


    /**
     * 执行实体的验证处理
     */
    @Note("执行实体的验证处理")
    public static Result validateOfEntity(Object obj, Class<?>[] groups) {
        try {
            if (obj instanceof Collection) {
                return validateOfEntityAry(obj, groups);
            } else if (obj instanceof Map) {
                return validateOfEntityMap(obj, groups);
            } else {
                return validateOfEntityOne(obj, groups);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Result validateOfEntityAry(Object obj, Class<?>[] groups) {
        Iterator iterator = ((Collection) obj).iterator();
        while (iterator.hasNext()) {
            Object val2 = iterator.next();

            if (val2 != null) {
                Result rst = validateOfEntity(val2, groups);

                if (rst.getCode() != Result.SUCCEED_CODE) {
                    return rst;
                }
            }
        }

        return Result.succeed();
    }

    private static Result validateOfEntityMap(Object obj, Class<?>[] groups) {
        Iterator iterator = ((Map) obj).values().iterator();
        while (iterator.hasNext()) {
            Object val2 = iterator.next();

            if (val2 != null) {
                Result rst = validateOfEntity(val2, groups);

                if (rst.getCode() != Result.SUCCEED_CODE) {
                    return rst;
                }
            }
        }

        return Result.succeed();
    }

    private static Result validateOfEntityOne(Object obj, Class<?>[] groups) throws IllegalAccessException {
        if (obj == null) {
            //null，由 @NotNull 来验证
            return Result.succeed();
        }

        ClassWrap cw = ClassWrap.get(obj.getClass());
        StringBuilder tmp = new StringBuilder();


        for (Map.Entry<String, FieldWrap> kv : cw.getFieldAllWraps().entrySet()) {
            Field field = kv.getValue().field;

            for (Annotation anno : kv.getValue().annoS) {
                Validator valid = ValidatorManager.get(anno.annotationType());

                if (valid != null) {

                    tmp.setLength(0);
                    Result rst = valid.validateOfValue(anno, field.get(obj), tmp);

                    if (rst.getCode() != Result.SUCCEED_CODE) {
                        if (Utils.isEmpty(rst.getDescription())) {
                            rst.setDescription(cw.clz().getSimpleName() + "." + field.getName());
                        }

                        if (rst.getData() instanceof BeanValidateInfo == false) {
                            rst.setData(new BeanValidateInfo(anno, valid.message(anno)));
                        }

                        return rst;
                    }
                }
            }
        }

        return Result.succeed();
    }


    /**
     * 执行错误处理
     */
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
