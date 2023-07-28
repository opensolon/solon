package demo;

import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Result;

import java.util.*;

/**
 * @author noear 2023/7/28 created
 */
@Configuration
public class DemoController {
    @Mapping("/actuator/meterRegistry")
    public Result demo(){
        List<String> map = new ArrayList<>();

        for (MeterRegistry meterRegistry : Metrics.globalRegistry.getRegistries()) {
            map.add(meterRegistry.getClass().getName());
        }

        return Result.succeed(map);
    }

    @Mapping("/actuator/metrics")
    public Result demo2(){
        Map<String, Object> map = new LinkedHashMap<>();

        Metrics.globalRegistry.getMeters().forEach(item->{
            List<MeterItem> list = new ArrayList<>();
            for (Measurement measurement : item.measure()) {
                list.add(new MeterItem(measurement.getStatistic().name(), measurement.getValue()));
            }
            map.put(item.getId().getName(), list);
        });

        return Result.succeed(map);
    }
}
