package org.noear.solon.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;

/**
 * 变量容器（主要在 BeanInjector 中使用）
 *
 * <pre><code>
 * //@Db 注入器添加
 * context.beanInjectorAdd(Db.classs, (varH, anno)->{
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
     * 设置值
     */
    void setValue(Object val);

    /**
     * 获取值
     * */
    Object getValue();

    /**
     * 是否为完成的
     * */
    boolean isDone();

    /**
     * 是否必须
     * */
    boolean required();

    /**
     * 设定必须
     * */
    void required(boolean required);
}
