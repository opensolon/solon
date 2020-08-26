package org.noear.solon.extend.feign;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeignClient {
    String name() default "";
    String url() default "";
    String path() default "";
    Class<? extends FeignConfiguration> configuration() default FeignConfigurationDefault.class;
}
