package graphql.solon.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.noear.solon.annotation.Alias;

/**
 * @author fuzi1996
 * @since 2.3
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryMapping {

    /**
     * Customize the name of the GraphQL field to bind to.
     * <p>By default, if not specified, this is initialized from the method name.
     */
    @Alias("value")
    String field() default "";

    /**
     * Customizes the name of the source/parent type for the GraphQL field.
     * <p>By default, if not specified, it is derived from the class name of a
     * {@link DataFetchingEnvironment#getSource() source} argument injected into the handler
     * method.
     * <p>This attributed is supported at the class level and at the method level!
     * When used on both levels, the one on the method level overrides the one at the class level.
     */
    String typeName() default "Query";

    /**
     * Alias for {@link QueryMapping#field()}.
     */
    @Alias("field")
    String value() default "";

}
