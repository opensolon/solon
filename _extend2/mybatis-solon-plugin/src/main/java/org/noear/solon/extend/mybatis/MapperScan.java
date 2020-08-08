package org.noear.solon.extend.mybatis;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MapperScan {
    String basePackages();
    String sqlSessionFactoryRef();
}
