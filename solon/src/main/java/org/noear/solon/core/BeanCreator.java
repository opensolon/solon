package org.noear.solon.core;

import java.lang.annotation.Annotation;

/**
 * Bean 生成处理器
 * */
public interface BeanCreator<T extends Annotation> {
    void handler(Class<?> clz, BeanWrap wrap, T anno) throws Exception;
}
