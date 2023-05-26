package org.noear.solon.test.annotation;

import java.lang.annotation.*;

/**
 * 配置测试属性源
 *
 * @author noear
 * @since 1.10
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TestPropertySource {
    String[] value();
}