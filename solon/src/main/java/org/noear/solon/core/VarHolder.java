package org.noear.solon.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;

/**
 * 变量容器
 * */
public interface VarHolder {

    ParameterizedType getGenericType();

    /**
     * 类型
     */
    Class<?> getType();

    /**
     * 注解
     */
    Annotation[] getAnnoS();

    /**
     * 设值
     */
    void setValue(Object val);
}
