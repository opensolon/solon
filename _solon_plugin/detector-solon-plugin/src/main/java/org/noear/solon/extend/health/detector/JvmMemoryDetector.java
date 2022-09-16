package org.noear.solon.extend.health.detector;

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

        info.put("total", formatByteSize(total));
        info.put("used", formatByteSize(used));
        info.put("usable", formatByteSize(usable));
        info.put("free", formatByteSize(free));
        return info;
    }
}
