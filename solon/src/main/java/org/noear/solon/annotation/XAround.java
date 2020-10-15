package org.noear.solon.annotation;

import org.noear.solon.core.MethodHandler;

import java.lang.annotation.*;

/**
 * 触发器：围绕处理（替代处理）
 *
 * @author noear
 * @since 1.0.20
 * */
//@Inherited //要可继承 // for type, 才有继承的意义
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XAround {
    /**
     * 调用处理程序
     * */
    Class<? extends MethodHandler> value();
    /**
     * 调用顺位
     * */
    int index() default 0;
}
