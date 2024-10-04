package org.noear.solon.web.webservices;

import java.lang.annotation.*;

/**
 * WS 服务引用
 *
 * @author noear
 * @since 1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebServiceReference {
    /**
     * 服务地址
     * */
    String value();
}
