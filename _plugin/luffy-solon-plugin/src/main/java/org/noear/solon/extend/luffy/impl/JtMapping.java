package org.noear.solon.extend.luffy.impl;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 文件与执行器印射关系
 *
 * @author noear
 * @since 1.3
 */
public class JtMapping {
    static Map<String, String> fileMapping = new LinkedHashMap<>();

    static {
        fileMapping.put("", "javascript");
        fileMapping.put("js", "javascript");
    }

    public static void addMaping(String suffix, String actuator) {
        fileMapping.put(suffix, actuator);
    }

    public static String getActuator(String suffix) {
        return fileMapping.get(suffix);
    }
}
