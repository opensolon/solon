package org.noear.solon.health.detector;

import java.util.*;

/**
 * Detector 管理器
 *
 * @author noear
 * @since 2.1
 */
public class DetectorManager {
    private static final Map<String,Detector> detectorMap = new LinkedHashMap<>();

    public static Collection<Detector> all(){
        return Collections.unmodifiableCollection(detectorMap.values());
    }
    public static void add(Detector detector){
        detectorMap.put(detector.getName(), detector);
    }

    public static Detector get(String name){
        return detectorMap.get(name);
    }

    public static void remove(String name){
        detectorMap.remove(name);
    }
}
