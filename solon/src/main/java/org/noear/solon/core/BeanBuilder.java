package org.noear.solon.core;

import java.lang.annotation.Annotation;

public interface BeanBuilder {
    Object build(Class<?> clz, Annotation[] fAnnoS);
}
