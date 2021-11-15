package org.noear.solon.extend.health.detector.integration;

import org.noear.solon.Solon;
import org.noear.solon.extend.health.detector.*;

import java.util.*;

/**
 * 健康探测器
 *
 * @author noear
 */
public class MachineDetector implements Detector {
    private static final Detector[] allDetectors = new Detector[]{
            new CpuDetector(),
            new JvmMemoryDetector(),
            new OsDetector(),
            new QpsDetector(),
            new MemoryDetector(),
            new DiskDetector()
    };

    private Set<Detector> detectors;

    @Override
    public void start() {
        if(detectors == null){
            return;
        }

        String detectorNamesStr = Solon.cfg().get("solon.health.detector");
        Set<String> detectorNames = new HashSet<>(Arrays.asList(detectorNamesStr.split(",")));

        if (detectorNames.size() == 0) {
            return;
        }

        detectors = new HashSet<>(detectorNames.size());

        for (Detector detector : allDetectors) {
            if (detectorNames.contains("*") || detectorNames.contains(detector.getName())) {
                detector.start();
                detectors.add(detector);
            }
        }
    }

    @Override
    public String getName() {
        return "machine";
    }

    @Override
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new LinkedHashMap<>();

        for (Detector detector : detectors) {
            info.put(detector.getName(), detector.getInfo());
        }

        return info;
    }
}
