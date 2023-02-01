package org.noear.solon.cloud.extend.powerjob;

import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于 name 任务管理
 *
 * @see org.noear.solon.cloud.extend.powerjob.impl.ProcessorFactoryOfSolon#getBean(String)
 *
 * @author noear
 * @since 2.0
 */
public class JobManager {
    static Map<String, BasicProcessor> jobMap = new HashMap<>();

    public static boolean containsJob(String name) {
        return jobMap.containsKey(name);
    }

    public static void addJob(String name, BasicProcessor handler) {
        jobMap.put(name, handler);
    }

    public static BasicProcessor getJob(String name) {
        return jobMap.get(name);
    }
}
