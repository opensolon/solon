package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 配置器（主要为了动态构建XBean）
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XConfiguration {

}
