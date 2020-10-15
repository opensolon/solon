package org.noear.solon.core;

import java.lang.annotation.Annotation;

/**
 * Bean 注入器
 *
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface BeanInjector<T extends Annotation> {
    void inject(VarHolder varH, T anno);
}
