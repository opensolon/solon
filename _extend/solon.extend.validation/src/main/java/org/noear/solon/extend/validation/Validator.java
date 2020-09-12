package org.noear.solon.extend.validation;

import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;

import java.lang.annotation.Annotation;

/**
 *
 * @author noear
 * @since 1.0.26
 * */
@FunctionalInterface
public interface Validator<T extends Annotation> {
    default String message(T anno) {
        return "";
    }

    XResult validate(XContext ctx, T anno, String name, StringBuilder tmp);
}
