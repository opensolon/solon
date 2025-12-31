/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.validation;

import org.noear.eggg.ClassEggg;
import org.noear.eggg.FieldEggg;
import org.noear.eggg.MethodEggg;
import org.noear.solon.Utils;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.core.util.EgggUtil;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.core.util.DataThrowable;
import org.noear.solon.validation.annotation.*;
import org.noear.solon.validation.annotation.Date;
import org.noear.solon.validation.util.FormatUtils;

import java.lang.annotation.Annotation;
import java.util.*;

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
     * 是否开启所有验证（默认：开启）
     * <p>
     * 开启后，将对所有的验证注解进行逐一验证
     * 关闭后，只要有一个校验不通过，就会停止后续校验，直接返回错误
     */
    public static boolean VALIDATE_ALL = false;

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
     * 设定实体验证器
     */
    public static void setBeanValidator(BeanValidator validator) {
        ValidatedValidator.instance.setValidator(validator);
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
        register(AssertTrue.class, AssertTrueValidator.instance);
        register(AssertFalse.class, AssertFalseValidator.instance);

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
        register(Range.class, RangeValidator.instance);

        register(Whitelist.class, WhitelistValidator.instance);
        register(Logined.class, LoginedValidator.instance);

        register(NotBlacklist.class, NotBlacklistValidator.instance);

        register(Validated.class, ValidatedValidator.instance);
    }

    /**
     * 清除所有验证器
     */
    public static void clear() {
        validMap.clear();
    }

    /**
     * 移除某个类型的验证器
     */
    public static <T extends Annotation> void remove(Class<T> type) {
        validMap.remove(type);
    }

    /**
     * 注册验证器
     */
    public static <T extends Annotation> void register(Class<T> type, Validator<T> validator) {
        validMap.put(type, validator);
    }

    /**
     * 移除某个类型的验证器
     */
    public static <T extends Annotation> Validator<T> get(Class<T> type) {
        return validMap.get(type);
    }

    /**
     * 执行上下文的验证处理
     */
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

    /**
     * 执行参数的验证处理
     */
    public static void validateOfInvocation(Invocation inv) throws Throwable {
        StringBuilder tmp = new StringBuilder();
        Result<List<BeanValidateInfo>> result = Result.succeed();
        result.setData(new ArrayList<>());

        for (int i = 0, len = inv.args().length; i < len; i++) {
            ParamWrap pw = inv.method().getParamWraps()[i];

            for (Annotation anno : pw.getParameter().getAnnotations()) {
                validateOfValue0(pw.spec().getName(), anno, inv.args()[i], result, tmp, inv, pw);
            }
        }

        if (result.getCode() != Result.SUCCEED_CODE) {
            tmp.setLength(0);
            for (BeanValidateInfo datum : result.getData()) {
                tmp.append(datum.message).append("；");
            }

            if (tmp.length() > 0) {
                tmp.setLength(tmp.length() - 1);
            }

            result.setDescription(tmp.toString());
            throw new ValidatorException(result.getCode(), result.getDescription(), null, result);
        }
    }

    private static void validateOfValue0(String label, Annotation anno, Object val, Result<List<BeanValidateInfo>> result, StringBuilder tmp, Invocation inv, ParamWrap pw) {
        Validator valid = validMap.get(anno.annotationType());

        if (valid != null) {
            if (valid.isSupportValueType(pw.getType()) == false) {
                throw new IllegalStateException("@" + anno.annotationType().getSimpleName() + " not support the '" + pw.getName() + "' parameter as " + pw.getType().getSimpleName() + " type: " + inv.method().getMethod());
            }

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
                    result.getData().add(info);
                } else if (rst.getData() instanceof Collection) {
                    List<BeanValidateInfo> list = (List<BeanValidateInfo>) rst.getData();
                    result.getData().addAll(list);
                } else {
                    BeanValidateInfo beanValidateInfo = new BeanValidateInfo(label, anno, valid.message(anno));
                    message = beanValidateInfo.message;
                    rst.setData(beanValidateInfo);
                    result.getData().add(beanValidateInfo);
                }

                if (VALIDATE_ALL) {
                    result.setCode(rst.getCode());
                } else {
                    if (ValidatorManager.failureDo(Context.current(), anno, rst, message)) {
                        throw new DataThrowable();
                    } else {
                        //throw new IllegalArgumentException(rst.getDescription());
                        String msg = FormatUtils.format(anno, rst, message);
                        throw new ValidatorException(rst.getCode(), msg, null, rst);
                    }
                }
            }
        }
    }


    /**
     * 执行实体的验证处理
     */
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

        ClassEggg ce = EgggUtil.getClassEggg(obj.getClass());
        StringBuilder tmp = new StringBuilder();

        Result result = Result.succeed();
        List<BeanValidateInfo> list = new ArrayList<>();
        for (FieldEggg fe : ce.getAllFieldEgggs()) {
            for (Annotation anno : fe.getAnnotations()) {
                Validator valid = ValidatorManager.get(anno.annotationType());

                if (valid != null) {

                    if (inGroup(valid.groups(anno), groups) == false) {
                        continue;
                    }

                    if (valid.isSupportValueType(fe.getType()) == false) {
                        throw new IllegalStateException("@" + anno.annotationType().getSimpleName() + " not support the '" + fe.getName() + "' field as " + fe.getType().getSimpleName() + " type: " + ce.getType());
                    }

                    tmp.setLength(0);
                    Result rst = valid.validateOfValue(anno, fe.getValue(obj, false), tmp);

                    if (rst.getCode() != Result.SUCCEED_CODE) {
                        if (Utils.isEmpty(rst.getDescription())) {
                            rst.setDescription(ce.getType().getSimpleName() + "." + fe.getName());
                        }

                        if (VALIDATE_ALL) {
                            result.setCode(rst.getCode());
                            if (rst.getData() instanceof BeanValidateInfo) {
                                list.add((BeanValidateInfo) rst.getData());
                            } else if (rst.getData() instanceof Collection) {
                                List<BeanValidateInfo> list2 = (List<BeanValidateInfo>) rst.getData();
                                list.addAll(list2);
                            } else {
                                rst.setData(new BeanValidateInfo(fe.getName(), anno, valid.message(anno)));
                                list.add((BeanValidateInfo) rst.getData());
                            }
                        } else {
                            if (rst.getData() instanceof BeanValidateInfo == false) {
                                rst.setData(new BeanValidateInfo(fe.getName(), anno, valid.message(anno)));
                            }
                            return rst;
                        }
                    }
                }
            }
        }

        //遍历方法进行校验
        for (MethodEggg me : ce.getPublicMethodEgggs()) {
            //只处理无参方法
            if (me.getParamCount() == 0) {
                for (Annotation anno : me.getAnnotations()) {
                    Validator valid = ValidatorManager.get(anno.annotationType());

                    if (valid != null) {

                        if (inGroup(valid.groups(anno), groups) == false) {
                            continue;
                        }

                        if (valid.isSupportValueType(me.getReturnType()) == false) {
                            throw new IllegalStateException("@" + anno.annotationType().getSimpleName() + " not support the '" + me.getName() + "' method as " + me.getReturnType().getSimpleName() + " type: " + ce.getType());
                        }

                        tmp.setLength(0);
                        Object methodValue = null;
                        try {
                            methodValue = me.invoke(obj);
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                        Result rst = valid.validateOfValue(anno, methodValue, tmp);

                        if (rst.getCode() != Result.SUCCEED_CODE) {
                            if (Utils.isEmpty(rst.getDescription())) {
                                rst.setDescription(ce.getType().getSimpleName() + "." + me.getName() + "()");
                            }

                            if (VALIDATE_ALL) {
                                result.setCode(rst.getCode());
                                if (rst.getData() instanceof BeanValidateInfo) {
                                    list.add((BeanValidateInfo) rst.getData());
                                } else if (rst.getData() instanceof Collection) {
                                    List<BeanValidateInfo> list2 = (List<BeanValidateInfo>) rst.getData();
                                    list.addAll(list2);
                                } else {
                                    rst.setData(new BeanValidateInfo(me.getName() + "()", anno, valid.message(anno)));
                                    list.add((BeanValidateInfo) rst.getData());
                                }
                            } else {
                                if (rst.getData() instanceof BeanValidateInfo == false) {
                                    rst.setData(new BeanValidateInfo(me.getName() + "()", anno, valid.message(anno)));
                                }
                                return rst;
                            }
                        }
                    }
                }
            }
        }

        result.setData(list);
        return result;
    }


    /**
     * 执行错误处理
     *
     * @return 当为 true，则以 DataThrowable 抛出；否则用 AuthorizationException 抛出。
     */
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

    private static boolean inGroup(Class<?>[] annoGroups, Class<?>[] groups) {
        if (annoGroups == null || annoGroups.length == 0) {
            return true;
        } else {
            if (groups == null || groups.length == 0) {
                return false;
            } else {
                for (Class<?> g1 : groups) {
                    for (Class<?> g2 : annoGroups) {
                        if (g1 == g2) {
                            return true;
                        }
                    }
                }

                return false;
            }
        }
    }
}