package org.noear.solon.extend.data.annotation;

import org.noear.solon.extend.data.tran.TranIsolation;
import org.noear.solon.extend.data.tran.TranPolicy;
import org.noear.solon.extend.data.around.TranInterceptor;
import org.noear.solon.annotation.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author noear
 * @since 1.0
 * */
//@Around(value = TranInterceptor.class, index = 20)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Tran {
    /**
     * 事务传导策略
     * */
    TranPolicy policy() default TranPolicy.required;

    /*
    * 事务隔离等级
    * */
    TranIsolation isolation() default TranIsolation.unspecified;

    /**
     * 只读事务
     * */
    boolean readOnly() default false;
}
