package org.noear.solon.extend.cors.annotation;

import org.noear.solon.annotation.Before;
import org.noear.solon.annotation.Note;
import org.noear.solon.annotation.Options;
import org.noear.solon.extend.cors.CrossOriginInterceptor;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.3
 */
@Deprecated
@Options //添加 Options
@Inherited
@Before({CrossOriginInterceptor.class})
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CrossOrigin {
    @Note("支持配置模式： ${xxx}")
    String origins() default "*";

    /**
     * 检测有效时间。默认：10分钟检测一次
     * */
    int maxAge() default 3600;

    /**
     * 允许认证
     * */
    boolean credentials() default true;
}
