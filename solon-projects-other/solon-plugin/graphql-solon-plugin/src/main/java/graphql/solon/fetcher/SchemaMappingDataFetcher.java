package graphql.solon.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.wrap.ParamWrap;
import graphql.solon.resolver.argument.HandlerMethodArgumentResolver;
import graphql.solon.resolver.argument.HandlerMethodArgumentResolverCollect;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class SchemaMappingDataFetcher implements DataFetcher<Object> {

    private final AopContext context;
    private final BeanWrap wrap;
    private final Method method;
    private final ParamWrap[] paramWraps;
    private final HandlerMethodArgumentResolverCollect collect;
    private final Map<ParamWrap, HandlerMethodArgumentResolver> argumentResolverCache;
    private final boolean isBatch;

    public SchemaMappingDataFetcher(AopContext context, BeanWrap wrap, Method method,
            boolean isBatch) {
        this.context = context;
        this.wrap = wrap;
        this.method = method;
        this.collect = this.context
                .getBean(HandlerMethodArgumentResolverCollect.class);
        this.argumentResolverCache = new ConcurrentHashMap<>(256);

        Parameter[] parameters = this.method.getParameters();
        if (Objects.nonNull(parameters) && parameters.length > 0) {
            this.paramWraps = new ParamWrap[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                this.paramWraps[i] = new ParamWrap(parameters[i]);
            }
        } else {
            this.paramWraps = null;
        }

        this.isBatch = isBatch;
    }

    /**
     * 构建执行参数
     */
    protected Object[] buildArgs(DataFetchingEnvironment environment) throws Exception {
        Object[] arguments = new Object[this.paramWraps.length];

        if (this.getMethodArgLength() > 0) {

            for (int i = 0; i < this.paramWraps.length; i++) {
                ParamWrap paramWrap = this.paramWraps[i];
                arguments[i] = this
                        .getArgument(environment, this.method, this.paramWraps, i, paramWrap);
            }
        }

        return arguments;
    }

    protected Object getArgument(DataFetchingEnvironment environment, Method method,
            ParamWrap[] paramWraps,
            int index,
            ParamWrap paramWrap) throws Exception {

        HandlerMethodArgumentResolver resolver = this.argumentResolverCache
                .get(paramWrap);
        if (Objects.isNull(resolver)) {
            List<HandlerMethodArgumentResolver> allCollector = this.collect.getAllCollector();
            // 从后往前判断,这样可以使得后加的优先级高
            for (int i = allCollector.size() - 1; i >= 0; i--) {
                HandlerMethodArgumentResolver item = allCollector.get(i);
                if (item.supportsParameter(method, paramWrap)) {
                    resolver = item;
                    this.argumentResolverCache.put(paramWrap, resolver);
                    break;
                }
            }
        }
        if (Objects.nonNull(resolver)) {
            return resolver.resolveArgument(environment, method, paramWraps, index, paramWrap);
        }

        throw new IllegalArgumentException("not support resolve method argument");
    }

    private int getMethodArgLength() {
        if (Objects.nonNull(this.paramWraps)) {
            return this.paramWraps.length;
        } else {
            return 0;
        }
    }

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        Object[] objects = this.buildArgs(environment);
        return this.method.invoke(wrap.get(), objects);
    }

}
