package micrometer.handler;

import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Metrics;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.micrometer.MeterItem;
import org.noear.solon.micrometer.MetricsResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局注册器的通用报文
 *
 * @author bai
 * @date 2023/07/27
 */
public class MetricsHandler implements Handler {

    public static final String URI = "/actuator/metrics";

    private static final MetricsHandler instance = new MetricsHandler();

    private static final Options options = Options.def().add(Feature.EnumUsingName);

    /**
     * 获取实例
     */
    public static MetricsHandler getInstance() {
        return instance;
    }


    /**
     * 处理
     *
     * @param ctx ctx
     */
    @Override
    public void handle(Context ctx){
        Map<String, Object> map = new LinkedHashMap<>();
        Metrics.globalRegistry.getMeters().forEach(item->{
            List<MeterItem> list = new ArrayList<>();
            for (Measurement measurement : item.measure()) {
                list.add(new MeterItem(measurement.getStatistic().name(), measurement.getValue()));
            }
            map.put(item.getId().getName(), list);
        });
        MetricsResult<?> result = MetricsResult.ok(map);
        ctx.outputAsJson(ONode.stringify(result, options));
    }
}
