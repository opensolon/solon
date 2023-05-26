package feign.solon;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeignClient {
    String url() default "";
    String group() default "";
    String name() default "";
    String path() default "";
    Class<? extends FeignConfiguration> configuration() default FeignConfigurationDefault.class;

    /**
     * 容错处理
     * */
    Class<?> fallback() default void.class;

    /**
     * 容错处理工厂
     * */
    Class<?> fallbackFactory() default void.class;
}
