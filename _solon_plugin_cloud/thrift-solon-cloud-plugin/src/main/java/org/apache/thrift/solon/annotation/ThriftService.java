package org.apache.thrift.solon.annotation;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.10
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ThriftService {
}
