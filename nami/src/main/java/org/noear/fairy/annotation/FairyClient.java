package org.noear.fairy.annotation;

import org.noear.fairy.FairyConfiguration;
import org.noear.fairy.FairyConfigurationDefault;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FairyClient {
    /**
     * uri:
     * 1. http://x.x.x/x/x/ (url)
     * 2. name:/x/x/ (name:path)
     * 3. /x/x/ (path)
     * */
    String value() default "";
    /**
     * 添加头信息
     * */
    String[] headers() default {};
    /**
     * 指定配置器
     * */
    Class<? extends FairyConfiguration> configuration() default FairyConfigurationDefault.class;
}

