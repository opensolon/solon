package org.noear.solon.data.dynamicds;

import java.lang.annotation.*;

/**
 * 切换动态数据源
 *
 * @author noear
 * @since 1.11
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicDs {
    String value() default "";
}
