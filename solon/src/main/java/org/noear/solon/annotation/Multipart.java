package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 多分片请求申请
 *
 * 一般附加在动作上
 *
 * @author noear
 * @since 1.6
 * */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Multipart {
}
