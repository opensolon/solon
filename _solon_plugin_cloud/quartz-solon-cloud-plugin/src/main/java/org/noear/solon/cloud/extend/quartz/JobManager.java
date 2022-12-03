package org.noear.solon.cloud.extend.quartz;

import org.noear.solon.cloud.CloudJobHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.11
 */
public class JobManager {
    static Map<String, CloudJobHandler> jobMap = new HashMap<>();

    public static boolean containsJob(String name) {
        return jobMap.containsKey(name);
    }

    public static void addJob(String name, CloudJobHandler handler) {
        jobMap.put(name, handler);
    }

    public static CloudJobHandler getJob(String name) {
        return jobMap.get(name);
    }
}
