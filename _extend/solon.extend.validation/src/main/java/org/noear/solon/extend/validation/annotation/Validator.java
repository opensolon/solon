package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.XAction;
import org.noear.solon.core.XContext;

import java.lang.annotation.Annotation;

@FunctionalInterface
public interface Validator<T extends Annotation> {
    boolean verify(XContext ctx, XAction action, T anno, StringBuilder tmp);
}
