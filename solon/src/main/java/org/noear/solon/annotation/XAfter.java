package org.noear.solon.annotation;

import org.noear.solon.core.XHandler;
import java.lang.annotation.*;


/**
 * 后置处理
 * */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XAfter {
    Class<? extends XHandler>[] value();
}
