/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    @MeterSummary(value = "demo.summary", maxValue = 88, minValue = 1, percentiles = {0.0, 0.2, 1.0})
    public Long summary() {
        return System.currentTimeMillis() % 100;
    }

    @Mapping("/timer")
    @MeterTimer(value = "demo.timer", description = "这是一个计时器")
    public String timer() {
        return "timer";
    }


    @Mapping("/longTimer")
    @MeterLongTimer(value = "demo.longTimer", description = "这是一个长任务计时器", percentiles = {0.0, 0.2, 1.0})
    public String longTimer() throws InterruptedException {
        Thread.sleep(100);
        return "longTimer";
    }
}
