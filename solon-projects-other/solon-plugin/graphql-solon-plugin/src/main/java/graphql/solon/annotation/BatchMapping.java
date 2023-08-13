package graphql.solon.annotation;

import org.noear.solon.annotation.Alias;

import java.lang.annotation.*;

/**
 * @author fuzi1996
 * @since 2.3
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BatchMapping {

    @Alias("value")
    String field() default "";

    String typeName() default "";

    @Alias("field")
    String value() default "";
}
