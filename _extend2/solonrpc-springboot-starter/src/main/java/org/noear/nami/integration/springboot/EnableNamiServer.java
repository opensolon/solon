package org.noear.nami.integration.springboot;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.3
 */
@Import({org.noear.solon.extend.springboot.AutoConfigurationSolon.class,
        org.noear.nami.integration.springboot.AutoConfigurationNami.class,
        org.noear.solon.cloud.integration.springboot.AutoConfigurationCloud.class})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableNamiServer {
}
