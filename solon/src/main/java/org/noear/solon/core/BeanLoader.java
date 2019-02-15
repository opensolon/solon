package org.noear.solon.core;

import java.lang.annotation.Annotation;

public interface BeanLoader<T extends Annotation> {
    void handler(BeanWrap wrap, T anno) throws Exception;
}
