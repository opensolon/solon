package org.noear.solon.core;

import java.lang.annotation.Annotation;

/**
 * 变量容器
 * */
public interface VarHolder {
    /**
     * 类型
     * */
    Class<?> getType();
    /**
     * 注解
     * */
    Annotation[] getAnnoS();
    /**
     * 设值
     * */
    void setValue(Object val);
}
