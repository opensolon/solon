package org.noear.solon.micrometer.Interceptor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.noear.snack.core.utils.StringUtil;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.micrometer.TagUtil;
import org.noear.solon.micrometer.annotation.MeterCounter;

import java.util.HashMap;
import java.util.Map;

/**
 * 计数器拦截器
 *
 * @author bai
 * @date 2023/07/21
 */
public class CounterInterceptor implements Interceptor {

    static final Map<String, Counter> counterCache = new HashMap<>();

    /**
     * 拦截
     *
     * @param inv 调用者
     * @return {@link Object}
     * @throws Throwable throwable
     */
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        MeterCounter anno = inv.target().getClass().getAnnotation(MeterCounter.class);
        // method
        if (anno == null) {
            anno = inv.method().getAnnotation(MeterCounter.class);
        }
        //此处为拦截处理
        Object rst = inv.invoke();
        // 计数
        if(anno != null && anno.enable()){
            if (!counterCache.containsKey(anno.value())){
                String counterName = anno.value();
                if (StringUtil.isEmpty(anno.value())){
                    counterName = inv.target().getClass() +"."+ inv.method().toString();
                }
                Counter counter = Metrics.globalRegistry.counter(counterName, TagUtil.tags(inv, anno.type(), anno.tags()));
                counterCache.put(anno.value(), counter);
            }
            counterCache.get(anno.value()).increment();
        }
        return rst;
    }
}
