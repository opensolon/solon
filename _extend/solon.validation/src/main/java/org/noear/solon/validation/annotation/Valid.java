package org.noear.solon.validation.annotation;

import org.noear.solon.annotation.Around;
import org.noear.solon.annotation.Before;
import org.noear.solon.validation.BeanValidateInterceptor;
import org.noear.solon.validation.ContextValidateInterceptor;

import java.lang.annotation.*;

/**
 * 验证触发器注解（可以把它加在基类上）
 *
 * @author noear
 * @since 1.0
 * */
@Inherited
@Before({ContextValidateInterceptor.class})
@Around(value = BeanValidateInterceptor.class, index = 1)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Valid {
}
