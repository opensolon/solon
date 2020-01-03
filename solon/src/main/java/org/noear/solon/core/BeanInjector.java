package org.noear.solon.core;

import java.lang.annotation.Annotation;

public interface BeanInjector<T extends Annotation> {
    void handler(FieldWrapTmp fwT, T anno);
}
