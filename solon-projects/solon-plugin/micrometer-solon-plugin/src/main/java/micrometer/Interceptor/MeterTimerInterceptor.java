package micrometer.Interceptor;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import org.noear.snack.core.utils.StringUtil;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.micrometer.TagUtil;
import org.noear.solon.micrometer.annotation.MeterTimer;


/**
 * 计计时器拦截器
 *
 * @author bai
 * @date 2023/07/27
 */
public class MeterTimerInterceptor implements Interceptor {


    /**
     * 做拦截
     *
     * @param inv 调用者
     * @return {@link Object}
     * @throws Throwable throwable
     */
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        MeterTimer anno = inv.method().getAnnotation(MeterTimer.class);

        Timer.Sample sample = Timer.start(Metrics.globalRegistry);
        //此处为拦截处理
        Object rst = inv.invoke();
        String counterName = anno.value();
        if (StringUtil.isEmpty(anno.value())) {
            counterName = inv.target().getClass() + "." + inv.method().toString();
        }
        sample.stop(Metrics.globalRegistry.timer(counterName, TagUtil.tags(inv, anno.type(), anno.tags())));
        return rst;
    }
}
