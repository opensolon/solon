package org.noear.solon.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;

/**
 * 变量容器（主要在 BeanInjector 中使用）
 *
 * <pre><code>
 * //@Db 注入器添加
 * Solon.context().beanInjectorAdd(Db.classs, (varH, anno)->{
 *     ...
 * });
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
public interface VarHolder {
    AopContext context();

    ParameterizedType getGenericType();

    /**
     * 是否为字段
     * */
    boolean isField();

    /**
     * 类型
     */
    Class<?> getType();

    /**
     * 注解
     */
    Annotation[] getAnnoS();

    /**
     * 获取完成名字
     * */
    String getFullName();

    /**
     * 设值
     */
    void setValue(Object val);
}
