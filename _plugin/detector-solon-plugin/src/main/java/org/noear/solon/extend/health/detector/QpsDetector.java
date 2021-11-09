package org.noear.solon.extend.health.detector;

import com.wujiuye.flow.FlowHelper;
import com.wujiuye.flow.FlowType;
import com.wujiuye.flow.Flower;
import org.noear.solon.Solon;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Qps 探测器
 *
 * @author 夜の孤城
 * @since 1.2
 * */
public class QpsDetector extends AbstractDetector {
    private FlowHelper flowHelper;

    @Override
    public void start() {
        if (flowHelper != null) {
            return;
        }

        flowHelper = new FlowHelper(FlowType.Second);

        Solon.global().filter((ctx, chain) -> {
            long start = System.currentTimeMillis();

            try {
                chain.doFilter(ctx);
            } catch (Throwable e) {
                flowHelper.incrException();
            } finally {
                flowHelper.incrSuccess(System.currentTimeMillis() - start);
            }
        });
    }

    @Override
    public String getName() {
        return "qps";
    }

    @Override
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new LinkedHashMap<>();

        Flower flower = flowHelper.getFlow(FlowType.Second);

        info.put("total", flower.total()); //总请求数
        info.put("totalException", flower.totalException()); //成功请求数
        info.put("totalSuccess", flower.totalSuccess()); //异常请求数
        info.put("argRt", flower.avgRt()); //平均请求耗时
        info.put("maxRt", flower.maxRt()); //最大请求耗时
        info.put("minRt", flower.minRt()); //最小请求耗时
        info.put("successAvg", flower.successAvg()); //平均请求成功数(每毫秒)
        info.put("exceptionAvg", flower.exceptionAvg()); //平均请求异常数(每毫秒)
        return info;
    }
}
