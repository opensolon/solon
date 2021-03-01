package org.noear.solon.extend.springboot.rpc;

import org.noear.solon.annotation.Import;

import java.lang.annotation.*;

/**
 * @author noear 2021/3/1 created
 */
@Import(AutoConfiguration.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableSolonRpc {
}
