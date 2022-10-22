package org.noear.solon.lang;

import java.lang.annotation.*;

/**
 * 不能为 Null（只是标识一下）
 *
 * @author noear
 * @since 1.10
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NonNull {
}
