package io.swagger.solon.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * 接口无需认证,生成文档不追加全局参数
 *
 * @since 2.3
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value={METHOD, TYPE})
public @interface ApiNoAuthorize {
}
