package org.noear.solon.extend.springboot;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 主要为Solon插件能在SpringBoot上运行
 *
 * @author noear 2020/12/28 created
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(ConfigurationSolon.class)
@Documented
public @interface SpringBootLinkSolon {
}
