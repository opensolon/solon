package org.noear.solon.cloud.extend.consul.detector;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.health.HealthChecker;

import java.util.*;

/**
 * 健康探测器
 *
 * @author noear
 */
public class HealthDetector {
    private static final Detector[] allDetectors = new Detector[]{
            new CpuDetector(),
            new JvmMemoryDetector(),
            new OsDetector(),
            new QpsDetector(),
            new MemoryDetector(),
            new DiskDetector()
    };
    Set<Detector> detectors = new HashSet<>();
    String detectorNamesStr;

    public HealthDetector(CloudProps cloudProps) {
        detectorNamesStr = cloudProps.getDiscoveryHealthDetector();
    }

    public void startDetect(SolonApp app) {
        if (Utils.isEmpty(detectorNamesStr)) {
            return;
        }

        Set<String> detectorNames = new HashSet<>(Arrays.asList(detectorNamesStr.split(",")));

        for (Detector detector : allDetectors) {
            if (detectorNames.contains(detector.getName())) {
                detectors.add(detector);
                if (detector instanceof QpsDetector) {
                    ((QpsDetector) detector).toDetect(app);
                }
            }
        }
    }

    public Map<String, Object> getInfo() {
        Map<String, Object> info = new HashMap<>();
        for (Detector detector : detectors) {
            info.put(detector.getName(), detector.getInfo());
        }
        return info;
    }

    static HealthDetector detector;

    public static void start(CloudProps cloudProps) {
        if (detector != null) {
            return;
        }

        //1.添加Solon服务，提供检测用
        //
        detector = new HealthDetector(cloudProps);
        detector.startDetect(Solon.global());

        HealthChecker.addIndicator("consul", () -> {
            return Result.succeed(detector.getInfo());
        });
    }
}
