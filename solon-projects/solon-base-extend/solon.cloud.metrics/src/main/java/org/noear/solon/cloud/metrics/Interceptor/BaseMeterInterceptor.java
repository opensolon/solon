package org.noear.solon.cloud.metrics.Interceptor;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.noear.solon.Utils;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.handle.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * 度量注解的拦截器基类
 *
 * @author bai
 * @since 2.4
 */
public abstract class BaseMeterInterceptor<T,M> implements Interceptor {
    protected final Map<T, M> meterCached = new HashMap<>();

    /**
     * 获取注解
     */
    protected abstract T getAnno(Invocation inv);

    /**
     * 获取注解名字
     */
    protected abstract String getAnnoName(T anno);

    /**
     * 度量
     */
    protected abstract Object metering(Invocation inv, T anno) throws Throwable;

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        //先在 method 找，再尝试在 class 找
        T anno = getAnno(inv);

        // 尝试计数
        if (anno != null) {
            return metering(inv, anno);
        } else {
            return inv.invoke();
        }
    }

    protected String getMeterName(Invocation inv, T anno) {
        String meterName = getAnnoName(anno);

        if (Utils.isEmpty(meterName)) {
            meterName = inv.target().getClass().getName() + "::" + inv.method().getMethod().getName();
        }

        return meterName;
    }

    protected Tags getMeterTags(Invocation inv, String[] annoTags) {
        Tags tags = Tags.of(annoTags);

        Context ctx = Context.current();

        if (ctx != null) {
            tags.and(Tag.of("uri", ctx.path()),
                    Tag.of("method", ctx.method()),
                    Tag.of("class", inv.target().getClass().getTypeName()),
                    Tag.of("executable", inv.method().getMethod().getName()));
        } else {
            tags.and(Tag.of("class", inv.target().getClass().getTypeName()),
                    Tag.of("executable", inv.method().getMethod().getName()));
        }


        return tags;
    }
}
