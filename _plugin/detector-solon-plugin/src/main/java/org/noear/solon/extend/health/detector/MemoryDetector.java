package org.noear.solon.extend.health.detector;

import com.sun.management.OperatingSystemMXBean;

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

        info.put("total", formatByteSize(total));
        info.put("used", formatByteSize(used));

        if (total > 0L) {
            float ratio = (float) used * 100.0F / (float) total;
            info.put("ratio", ratio);
        }
        return info;
    }
}
