package graphql.solon.resolver.argument;

import graphql.schema.DataFetchingEnvironment;
import java.lang.reflect.Method;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.lang.Nullable;

/**
 * @author fuzi1996
 * @since 2.3
 */
public interface HandlerMethodArgumentResolver {

    boolean supportsParameter(Method method, ParamWrap paramWrap);

    @Nullable
    Object resolveArgument(DataFetchingEnvironment environment, Method method,
            ParamWrap[] paramWraps, int index, ParamWrap paramWrap)
            throws Exception;
}
