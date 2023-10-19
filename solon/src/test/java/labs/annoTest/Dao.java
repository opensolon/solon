package labs.annoTest;

import java.lang.annotation.*;

/**
 * @author noear 2023/10/19 created
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
public @interface Dao {
    // Interface Mapper
}
