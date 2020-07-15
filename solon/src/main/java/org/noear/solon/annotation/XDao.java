package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * Dao组件
 *
 * 从XBean分离出来（避免添加别的特性时造成相互干扰）
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XDao {
}
