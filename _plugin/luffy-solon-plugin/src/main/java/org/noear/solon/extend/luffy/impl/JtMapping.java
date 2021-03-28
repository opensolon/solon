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
        fileMapping.put("btl","beetl");
        fileMapping.put("enj","enjoy");
        fileMapping.put("ftl","freemarker");
        fileMapping.put("thy","thymeleaf");
        fileMapping.put("vm","velocity");
        fileMapping.put("groovy", "groovy");
        fileMapping.put("lua", "lua");
        fileMapping.put("py","python");
        fileMapping.put("rb","ruby");
    }

    public static void addMaping(String suffix, String actuator) {
        fileMapping.put(suffix, actuator);
    }

    public static String getActuator(String suffix) {
        return fileMapping.get(suffix);
    }
}
