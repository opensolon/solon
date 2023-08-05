package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 配置属性源
 *
 * @author noear
 * @since 1.12
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface PropertySource {
    /**
     * 例，资源文件：classpath:demo.yml
     * 例，外部文件：./demo.yml
     * */
    String[] value();
}