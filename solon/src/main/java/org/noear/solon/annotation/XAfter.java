package org.noear.solon.annotation;

import org.noear.solon.core.XHandler;
import java.lang.annotation.*;


/**
 * 触发器：后置处理（仅争对 XController 和 XAction 的拦截器）
 *
 * @author noear
 * @since 1.0
 * */
@Inherited //要可继承
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XAfter {
    Class<? extends XHandler>[] value();
}
