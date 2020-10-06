package org.noear.solon.core;

import java.lang.annotation.Annotation;

/**
 * Bean 生成处理器
 *
 * @author noear
 * @since 1.0
 * */
public interface BeanBuilder<T extends Annotation> {
    void handler(Class<?> clz, BeanWrap wrap, T anno) throws Exception;
}
