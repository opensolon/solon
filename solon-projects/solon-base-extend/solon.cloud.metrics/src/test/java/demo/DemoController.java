package demo;

import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.cloud.metrics.AbsMeterRegistry;
import org.noear.solon.cloud.metrics.MeterItem;
import org.noear.solon.core.handle.Result;

import java.util.*;

/**
 * @author noear 2023/7/28 created
 */
@Configuration
public class DemoController {
    @Mapping("/actuator/meterRegistry")
    public Result demo(){
        Map<String, Object> map = new HashMap<>();

        for (AbsMeterRegistry<? extends MeterRegistry> meterRegistry : AbsMeterRegistry.meterRegistryList) {
            map.put(meterRegistry.getClass().getName(), meterRegistry.scrape());
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
