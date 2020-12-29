package org.noear.solon.extend.consul.detector;

import java.util.HashMap;
import java.util.Map;

public class OsDetector extends AbstractDetector {
    private String arch=System.getProperty("os.arch");
    private String version=System.getProperty("os.version");
    @Override
    public String getName() {
        return "os";
    }

    @Override
    public Map<String, Object> getInfo() {
        Map<String,Object> info=new HashMap<>();
        info.put("name",osName);
        info.put("arch", arch);
        info.put("version",version);
        return info;
    }
}
