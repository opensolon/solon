package org.noear.solon.validation;

import org.noear.solon.core.handle.Result;

import java.lang.annotation.Annotation;

/**
 * @author noear
 * @since 1.5
 */
public class BeanValidateInfo extends Result {
    public final Annotation anno;
    public final String message;

    public BeanValidateInfo(Annotation anno, String message) {
        this.anno = anno;
        this.message = message;
    }
}
