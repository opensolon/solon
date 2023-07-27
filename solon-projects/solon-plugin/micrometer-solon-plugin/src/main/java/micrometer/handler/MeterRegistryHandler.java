package micrometer.handler;

import io.micrometer.core.instrument.MeterRegistry;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.micrometer.AbsMeterRegistry;
import org.noear.solon.micrometer.MetricsResult;

import java.util.HashMap;
import java.util.Map;

/**
 * 每个注册器的报文
 *
 * @author bai
 * @date 2023/07/27
 */
public class MeterRegistryHandler implements Handler {


    public static final String URI = "/actuator/meterRegistry";

    private static final MeterRegistryHandler instance = new MeterRegistryHandler();

    private static final Options options = Options.def().add(Feature.EnumUsingName);

    /**
     * 获取实例
     */
    public static MeterRegistryHandler getInstance() {
        return instance;
    }


    /**
     * 处理
     *
     * @param context 上下文
     */
    @Override
    public void handle(Context context){
        Map<String, Object> map = new HashMap<>();
        for (AbsMeterRegistry<? extends  MeterRegistry> meterRegistry : AbsMeterRegistry.meterRegistryList) {
            map.put(meterRegistry.getClass().getName(), meterRegistry.scrape());
        }
        MetricsResult<?> result = MetricsResult.ok(map);
        context.outputAsJson(ONode.stringify(result, options));
    }

}
