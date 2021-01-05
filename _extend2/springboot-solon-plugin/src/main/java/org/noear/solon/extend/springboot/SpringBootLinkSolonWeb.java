package org.noear.solon.extend.springboot;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 主要为Solon插件能在SpringBoot上运行
 *
 * @author noear
 * @since 1.2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({ConfigurationSolon.class,ConfigurationSolonWeb.class})
@Documented
public @interface SpringBootLinkSolonWeb {
}
