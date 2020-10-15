package org.noear.solon.core;

import java.lang.annotation.Annotation;

/**
 * Bean 构建器
 *
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface BeanBuilder<T extends Annotation> {
    /**
     * 构建
     * */
    void build(Class<?> clz, BeanWrap wrap, T anno) throws Exception;
}
