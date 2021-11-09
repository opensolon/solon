package org.noear.solon.extend.health.detector;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 系统 探测器
 *
 * @author 夜の孤城
 * @since 1.2
 * */
public class OsDetector extends AbstractDetector {
    private String arch = System.getProperty("os.arch");
    private String version = System.getProperty("os.version");

    @Override
    public String getName() {
        return "os";
    }

    @Override
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("name", osName);
        info.put("arch", arch);
        info.put("version", version);
        return info;
    }
}
