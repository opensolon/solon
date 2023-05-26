package org.noear.solon.validation;

import org.noear.solon.core.handle.Result;

import java.lang.annotation.Annotation;

/**
 * 验证器异常
 *
 * @author noear
 * @since 1.4
 */
public class ValidatorException extends RuntimeException {
    private int code;
    private Annotation annotation;
    private Result result;

    /**
     * 获取状态码
     * */
    public int getCode() {
        return code;
    }

    /**
     * 获取触发的注解
     * */
    public Annotation getAnnotation() {
        return annotation;
    }

    /**
     * 获取结果
     * */
    public Result getResult() {
        return result;
    }

    public ValidatorException(int code, String message, Annotation annotation, Result result) {
        super(message);
        this.code = code;
        this.annotation = annotation;
        this.result = result;
    }
}
