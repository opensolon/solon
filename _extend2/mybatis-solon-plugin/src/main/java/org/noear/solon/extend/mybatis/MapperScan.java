package org.noear.solon.extend.mybatis;

import java.lang.annotation.*;

/**
 * MapperScan 的作用：
 * 扫描 @basePackages 里的类，
 * 然后 用 @sqlSessionFactoryRef 生成 mapper 实例
 * 最后 注册到 bean 管理中心
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MapperScan {
    String basePackages();
    String sqlSessionFactoryRef();
}
