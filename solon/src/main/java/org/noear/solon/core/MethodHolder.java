package org.noear.solon.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 函数容器
 * */
public interface MethodHolder {
    /**
     * 获取函数
     * */
    Method getMethod();
    /**
     * 获取函数参数
     */
    Parameter[] getParameters();
    /**
     * 获取函数反回类型
     */
    Class<?> getReturnType();

    /**
     * 获取函数所有注解
     * */
    Annotation[] getAnnotations();

    /**
     * 获取函数某种注解
     * */
    <T extends Annotation> T getAnnotation(Class<T> type);
}
