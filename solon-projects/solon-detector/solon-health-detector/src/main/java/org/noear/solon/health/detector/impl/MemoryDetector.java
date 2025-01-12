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

import com.sun.management.OperatingSystemMXBean;
import org.noear.solon.health.detector.AbstractDetector;
import org.noear.solon.health.detector.util.SizeUtil;

import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 内存 探测器
 *
 * @author 夜の孤城
 * @since 1.2
 * */
public class MemoryDetector extends AbstractDetector {
    @Override
    public String getName() {
        return "memory";
    }

    @Override
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new LinkedHashMap<>();

        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        long total = osmxb.getTotalPhysicalMemorySize();
        long free = osmxb.getFreePhysicalMemorySize();
        long used = total - free;

        info.put("total", SizeUtil.formatByteSize(total));
        info.put("used", SizeUtil.formatByteSize(used));

        if (total > 0L) {
            float ratio = (float) used * 100.0F / (float) total;
            info.put("ratio", ratio);
        }
        return info;
    }
}
