package graphql.solon.annotation;

import org.noear.solon.annotation.Alias;

/**
 * @author fuzi1996
 * @since 2.3
 */
public @interface BatchMapping {

    @Alias("value")
    String field() default "";

    String typeName() default "";

    @Alias("field")
    String value() default "";
}
