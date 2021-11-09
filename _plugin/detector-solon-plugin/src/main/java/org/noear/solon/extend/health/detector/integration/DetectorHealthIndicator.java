package org.noear.solon.extend.health.detector.integration;

import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.health.HealthIndicator;
import org.noear.solon.extend.health.detector.*;

import java.util.*;

/**
 * 健康探测器
 *
 * @author noear
 */
public class DetectorHealthIndicator implements HealthIndicator {
    private static final Detector[] allDetectors = new Detector[]{
            new CpuDetector(),
            new JvmMemoryDetector(),
            new OsDetector(),
            new QpsDetector(),
            new MemoryDetector(),
            new DiskDetector()
    };

    private Set<Detector> detectors;


    /**
     * 开始生周期
     */
    public DetectorHealthIndicator(String detectorNamesStr) {
        Set<String> detectorNames = new HashSet<>(Arrays.asList(detectorNamesStr.split(",")));

        if (detectorNames.size() == 0) {
            return;
        }

        detectors = new HashSet<>(detectorNames.size());

        for (Detector detector : allDetectors) {
            if (detectorNames.contains(detector.getName())) {
                detector.start();
                detectors.add(detector);
            }
        }
    }


    /**
     * 获取健建指标
     */
    @Override
    public Result get() {
        if (detectors == null) {
            return Result.succeed();
        } else {
            Map<String, Object> info = new HashMap<>();
            for (Detector detector : detectors) {
                info.put(detector.getName(), detector.getInfo());
            }

            return Result.succeed(info);
        }
    }
}
