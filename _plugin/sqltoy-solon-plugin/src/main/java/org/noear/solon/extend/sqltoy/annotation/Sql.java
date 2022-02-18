package org.noear.solon.extend.sqltoy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于Mapper接口上
 * @author 夜の孤城
 * @since 1.5
 * */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Sql {
    String value();

    /**
     * 是否批量操作，目前只适用于更新。且如果是batch模式，只取其中的第一个列表参数为作为操作的数据
     * @return
     */
    boolean batch() default false;
}
