package org.noear.solon.extend.powerjob;

import org.noear.solon.core.BeanWrap;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于 name 任务包装类管理
 *
 * @author noear
 * @since 2.0
 */
public class JobBeanManager {
    static Map<String, BeanWrap> jobMap = new HashMap<>();

    public static boolean containsJob(String name) {
        return jobMap.containsKey(name);
    }

    public static void addJob(String name, BeanWrap beanWrap) {
        jobMap.put(name, beanWrap);
    }

    public static BeanWrap getJob(String name) {
        return jobMap.get(name);
    }
}
