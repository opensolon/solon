package org.noear.solon.cloud.extend.consul.detector;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

public class MemoryDetector extends AbstractDetector{
    @Override
    public String getName() {
        return "memory";
    }

    @Override
    public Map<String, Object> getInfo() {
        Map<String,Object> info=new HashMap<>();
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long total = osmxb.getTotalPhysicalMemorySize();
        long free = osmxb.getFreePhysicalMemorySize();
        long used = total - free;

        info.put("total",formatByteSize(total));
        info.put("used",formatByteSize(used));

        if (total > 0L) {
            float ratio = (float)used * 100.0F / (float)total;
            info.put("ratio",ratio);
        }
        return info;
    }
}
