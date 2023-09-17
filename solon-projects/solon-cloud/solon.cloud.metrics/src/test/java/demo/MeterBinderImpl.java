package demo;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

/**
 * @author noear 2023/8/5 created
 */
public class MeterBinderImpl implements MeterBinder {

    @Override
    public void bindTo(MeterRegistry meterRegistry) {
        Metrics.globalRegistry.add(meterRegistry);
    }
}
