package org.noear.fairy.annotation;

import org.noear.fairy.FairyConfiguration;
import org.noear.fairy.FairyConfigurationDefault;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FairyClient {
    String value() default "";
    String[] headers() default {};
    Class<? extends FairyConfiguration> configuration() default FairyConfigurationDefault.class;
}

