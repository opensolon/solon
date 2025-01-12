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

import org.noear.solon.health.detector.AbstractDetector;
import org.noear.solon.health.detector.util.SizeUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * jvm 探测器
 *
 * @author 夜の孤城
 * @since 1.2
 * */
public class JvmMemoryDetector extends AbstractDetector {
    @Override
    public String getName() {
        return "jvm";
    }

    @Override
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new LinkedHashMap<>();

        Runtime run = Runtime.getRuntime();

        long max = run.maxMemory();
        long total = run.totalMemory();
        long free = run.freeMemory();
        long used = total - free;
        long usable = max - used;

        if (max > 0L) {
            float ratio = (float) used * 100.0F / (float) max;
            info.put("ratio", ratio);
        }

        info.put("total", SizeUtil.formatByteSize(total));
        info.put("used", SizeUtil.formatByteSize(used));
        info.put("usable", SizeUtil.formatByteSize(usable));
        info.put("free", SizeUtil.formatByteSize(free));
        return info;
    }
}
