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
package org.noear.solon.health.detector.impl;

import com.wujiuye.flow.FlowHelper;
import com.wujiuye.flow.FlowType;
import com.wujiuye.flow.Flower;
import org.noear.solon.Solon;
import org.noear.solon.health.detector.AbstractDetector;

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

        Solon.app().filter((ctx, chain) -> {
            long start = System.currentTimeMillis();

            try {
                chain.doFilter(ctx);
                flowHelper.incrSuccess(System.currentTimeMillis() - start);
            } catch (Throwable e) {
                flowHelper.incrException();
                throw e;
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
