package org.noear.solon.extend.validation.annotation;

import org.noear.solon.annotation.Before;
import org.noear.solon.extend.validation.ValidateInterceptor;

import java.lang.annotation.*;

/**
 * 验证触发器注解（可以把它加在基类上）
 *
 * @author noear
 * @since 1.0
 * */
@Before({ValidateInterceptor.class})
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Valid {
}
