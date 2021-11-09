package org.noear.solon.extend.health.detector.integration;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.health.HealthChecker;
import org.noear.solon.extend.health.HealthIndicator;
import org.noear.solon.extend.health.detector.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author noear
 * @since 1.5
 */
public class XPluginImp implements Plugin {
    private static final Detector[] allDetectors = new Detector[]{
            new CpuDetector(),
            new JvmMemoryDetector(),
            new OsDetector(),
            new QpsDetector(),
            new MemoryDetector(),
            new DiskDetector()
    };

    @Override
    public void start(SolonApp app) {
        String detectorNamesStr = Solon.cfg().get("solon.Health.detector");
        if (Utils.isEmpty(detectorNamesStr)) {
            return;
        }

        Set<String> detectorNames = new HashSet<>(Arrays.asList(detectorNamesStr.split(",")));

        if (detectorNames.size() == 0) {
            return;
        }

        for (Detector detector : allDetectors) {
            if (detectorNames.contains(detector.getName())) {
                detector.start();
                HealthChecker.addIndicator(detector.getName(), detector);
            }
        }
    }
}
