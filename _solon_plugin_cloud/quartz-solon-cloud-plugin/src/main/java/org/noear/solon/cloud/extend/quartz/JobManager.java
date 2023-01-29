package org.noear.solon.cloud.extend.quartz;

import org.noear.solon.cloud.model.JobHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.11
 */
public class JobManager {
    static Map<String, JobHolder> jobMap = new HashMap<>();

    public static boolean containsJob(String name) {
        return jobMap.containsKey(name);
    }

    public static void addJob(String name, JobHolder handler) {
        jobMap.put(name, handler);
    }

    public static JobHolder getJob(String name) {
        return jobMap.get(name);
    }
}
