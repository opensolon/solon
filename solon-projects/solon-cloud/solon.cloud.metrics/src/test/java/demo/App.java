package demo;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.SolonMain;
import org.noear.solon.cloud.metrics.annotation.*;
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
    @MeterCounter(value = "demo.counter", unit = "个")
    public String counter() {
        return "counter";
    }

    @Mapping("/gauge")
    @MeterGauge(name = "demo.gauge", description = "这是一个gauge")
    public Long gauge() {
        return System.currentTimeMillis() % 100;
    }

    @Mapping("/summary")
    @MeterSummary(value = "demo.summary", maxValue = 88, minValue = 1, percentiles = {10, 20, 50})
    public Long summary() {
        return System.currentTimeMillis() % 100;
    }

    @Mapping("/timer")
    @MeterTimer(value = "demo.timer", description = "这是一个计时器", percentiles = {10.0, 20, 50})
    public String timer() {
        return "timer";
    }


    @Mapping("/timer")
    @MeterLongTimer(value = "demo.timer", description = "这是一个长任务计时器", percentiles = {10.0, 20, 50})
    public String longTimer() throws InterruptedException {
        Thread.sleep(100);
        return "longTimer";
    }
}
