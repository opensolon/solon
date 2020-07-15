package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * Service组件
 *
 * 从XBean分离出来（避免添加别的特性时造成相互干扰）（如 remoting 与 UapiGateway 的冲突）
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XService {
    boolean remoting() default false; //是否开始远程服务
}
