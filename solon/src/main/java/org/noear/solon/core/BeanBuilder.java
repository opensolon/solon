package org.noear.solon.core;

import java.lang.annotation.Annotation;

public interface BeanBuilder<T extends Annotation> {
    void build(Class<?> clz, FieldWrapTmp fwT, T anno);
}
