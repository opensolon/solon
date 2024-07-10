package org.noear.solon.validation;

import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.Result;
import org.noear.solon.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 验证器异常
 *
 * @author noear
 * @since 1.4
 */
public class ValidatorException extends StatusException {
    @Nullable
    private Method method;
    private Annotation annotation;
    private Result result;

    /**
     * 获取触发函数
     */
    public Method getMethod() {
        return method;
    }

    /**
     * 获取触发的注解
     */
    public Annotation getAnnotation() {
        return annotation;
    }

    /**
     * 获取结果
     */
    public Result getResult() {
        return result;
    }

    public ValidatorException(int code, String message, Annotation annotation, Result result) {
        this(code, message, annotation, result, null);
    }

    public ValidatorException(int code, String message, Annotation annotation, Result result, Method method) {
        super(message, code);
        this.method = method;
        this.annotation = annotation;
        this.result = result;
    }
}