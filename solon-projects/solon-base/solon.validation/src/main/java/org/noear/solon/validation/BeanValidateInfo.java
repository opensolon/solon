package org.noear.solon.validation;

import org.noear.solon.core.handle.Result;

import java.lang.annotation.Annotation;

/**
 * 实体验证结果信息
 *
 * @author noear
 * @since 1.5
 */
public class BeanValidateInfo extends Result {
    /**
     * 验证执行对应的注解
     * */
    public final Annotation anno;
    /**
     * 验证结果消息
     * */
    public final String message;

    public BeanValidateInfo(Annotation anno, String message) {
        this.anno = anno;
        this.message = message;
    }
}
