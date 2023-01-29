package org.noear.solon.extend.powerjob;

import org.noear.solon.core.BeanWrap;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于 name 任务管理
 *
 * @see org.noear.solon.cloud.extend.powerjob.impl.ProcessorFactoryOfSolon::getBean
 *
 * @author noear
 * @since 1.11
 */
public class JobBeanManager {
    static Map<String, BeanWrap> jobMap = new HashMap<>();

    public static boolean containsJob(String name) {
        return jobMap.containsKey(name);
    }

    public static void addJob(String name, BeanWrap handler) {
        jobMap.put(name, handler);
    }

    public static BeanWrap getJob(String name) {
        return jobMap.get(name);
    }
}
