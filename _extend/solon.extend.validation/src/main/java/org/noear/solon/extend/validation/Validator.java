package org.noear.solon.extend.validation;

import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;

import java.lang.annotation.Annotation;

@FunctionalInterface
public interface Validator<T extends Annotation> {
    default String message(T anno) {
        return "";
    }

    XResult validate(XContext ctx, T anno, StringBuilder tmp);
}
