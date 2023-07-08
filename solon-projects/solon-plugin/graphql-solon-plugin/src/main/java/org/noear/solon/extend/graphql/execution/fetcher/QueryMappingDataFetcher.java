package org.noear.solon.extend.graphql.execution.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.extend.graphql.getter.IValueGetter;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class QueryMappingDataFetcher implements DataFetcher<Object> {

    private final AopContext context;
    private final BeanWrap wrap;
    private final Method method;
    private final ParamWrap[] paramWraps;
    private IValueGetter valueGetter;

    public QueryMappingDataFetcher(AopContext context, BeanWrap wrap, Method method) {
        this.context = context;
        this.wrap = wrap;
        this.method = method;
        Parameter[] parameters = this.method.getParameters();
        if (Objects.nonNull(parameters) && parameters.length > 0) {
            this.paramWraps = new ParamWrap[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                this.paramWraps[i] = new ParamWrap(parameters[i]);
            }
        } else {
            this.paramWraps = null;
        }
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

    /**
     * 构建执行参数
     */
    protected Object[] buildArgs(DataFetchingEnvironment environment) {
        Object[] arguments = new Object[this.paramWraps.length];

        if (this.getMethodArgLength() > 1) {
            if (Objects.isNull(this.valueGetter)) {
                this.valueGetter = this.context.getBean(IValueGetter.class);
            }
            if (Objects.isNull(this.valueGetter)) {
                throw new IllegalStateException("can't find IValueGetter");
            }

            for (int i = 0; i < this.paramWraps.length; i++) {
                ParamWrap paramWrap = this.paramWraps[i];
                arguments[i] = this.valueGetter.get(environment, paramWrap);
            }
        }

        return arguments;
    }

    /**
     * 尝试将值转换为实体
     */
    private Object changeEntityDo(Context ctx, String name, Class<?> type) throws Exception {
        ClassWrap clzW = ClassWrap.get(type);
        Map<String, String> map = ctx.paramMap();

        return clzW.newBy(map::get, ctx);
    }
}
