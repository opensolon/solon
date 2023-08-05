package demo;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.cloud.metrics.export.MeterOpener;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.handle.Context;

/**
 * @author noear 2023/8/5 created
 */
@Configuration
public class MeterOpenerImpl implements MeterOpener {
    SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();

    @Bean
    public MeterRegistry registry(){
        return meterRegistry;
    }

    @Override
    public String path() {
        return "/demo/xxxx";
    }

    @Override
    public boolean isSupported(AopContext aopContext) {
        //不支持开放输出
        return false;
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        //meterRegistry
    }
}
