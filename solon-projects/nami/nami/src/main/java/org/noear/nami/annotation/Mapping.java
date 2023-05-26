package org.noear.nami.annotation;

import java.lang.annotation.*;

/**
 * 请求映射
 *
 * @author noear
 * @since 1.1
 * */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapping {
    /**
     * mapping:
     *
     * 例1：GET
     * 例2: GET /xxx/xxx
     * 例3: GET /xxx/{xxx}
     * */
    String value() default "";
    /**
     * 添加头信息
     *
     * 例：{"xxx=xxx","yyy=yyy"}
     * */
    String[] headers() default {};
}
