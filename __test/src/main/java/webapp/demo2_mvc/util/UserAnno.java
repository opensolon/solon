package webapp.demo2_mvc.util;

import java.lang.annotation.*;

/**
 * @author noear 2025/7/15 created
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UserAnno {
    String value() default "";
}
