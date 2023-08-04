package demo;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.SolonMain;
import org.noear.solon.cloud.metrics.annotation.MeterCounter;
import org.noear.solon.cloud.metrics.annotation.MeterGauge;
import org.noear.solon.cloud.metrics.annotation.MeterSummary;
import org.noear.solon.cloud.metrics.annotation.MeterTimer;

/**
 * 不需要配置，直接可用
 */
@Controller
@SolonMain
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args);
    }

    @Mapping("/counter")
    @MeterCounter("demo.counter")
    public String counter() {
        return "counter";
    }

    @Mapping("/gauge")
    @MeterGauge("demo.gauge")
    public Long gauge() {
        return System.currentTimeMillis() % 100;
    }

    @Mapping("/summary")
    @MeterSummary(value = "demo.summary", maxValue = 88, minValue = 1, percentiles = {10, 20, 50})
    public Long summary() {
        return System.currentTimeMillis() % 100;
    }

    @Mapping("/timer")
    @MeterTimer("demo.timer")
    public String timer() {
        return "timer";
    }
}
