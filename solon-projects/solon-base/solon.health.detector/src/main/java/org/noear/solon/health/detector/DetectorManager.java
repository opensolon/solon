package org.noear.solon.health.detector;

import java.util.*;

/**
 * Detector 管理器
 *
 * @author noear
 * @since 2.1
 */
public class DetectorManager {
    private static final Map<String, Detector> detectorMap = new LinkedHashMap<>();

    /**
     * 添加
     */
    public static void add(Detector detector) {
        detectorMap.put(detector.getName(), detector);
    }

    /**
     * 移除
     */
    public static void remove(String name) {
        detectorMap.remove(name);
    }

    /**
     * 获取
     */
    public static Detector get(String name) {
        return detectorMap.get(name);
    }


    /**
     * 获取全部
     */
    public static Collection<Detector> all() {
        return detectorMap.values();
    }


    /**
     * 获取多个
     *
     * @since 2.4
     */
    public static Map<String, Detector> getMore(String... names) {
        Map<String, Detector> tmp = new HashMap<>();

        for (String name : names) {
            if ("*".equals(name)) {
                for (Detector detector : detectorMap.values()) {
                    tmp.put(detector.getName(), detector);
                }
                break;
            } else {
                Detector detector = get(name);
                if (detector != null) {
                    tmp.put(detector.getName(), detector);
                }
            }
        }

        return tmp;
    }

    /**
     * 启动
     * */
    public static void start(String... names) throws Throwable{
        for (String name : names) {
            if ("*".equals(name)) {
                for (Detector detector : detectorMap.values()) {
                    detector.start();
                }
                break;
            } else {
                Detector detector = get(name);
                if (detector != null) {
                    detector.start();
                }
            }
        }
    }
}