package org.noear.solon.health.detector.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppBeanLoadEndEvent;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.health.HealthChecker;
import org.noear.solon.health.detector.Detector;
import org.noear.solon.health.detector.DetectorManager;
import org.noear.solon.health.detector.impl.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author noear
 * @since 1.5
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        String detectorNamesStr = Solon.cfg().get("solon.health.detector");
        if (Utils.isEmpty(detectorNamesStr)) {
            return;
        }

        Set<String> detectorNames = new HashSet<>(Arrays.asList(detectorNamesStr.split(",")));

        if (detectorNames.size() == 0) {
            return;
        }


        DetectorManager.add(new CpuDetector());
        DetectorManager.add(new JvmMemoryDetector());
        DetectorManager.add(new OsDetector());
        DetectorManager.add(new QpsDetector());
        DetectorManager.add(new MemoryDetector());
        DetectorManager.add(new DiskDetector());

        context.subBeansOfType(Detector.class, detector -> {
            DetectorManager.add(detector);
        });

        //晚点启动，让扫描时产生的组件可以注册进来
        EventBus.subscribe(AppBeanLoadEndEvent.class, e -> {
            onLoaded(detectorNames);
        });
    }

    private void onLoaded(Set<String> detectorNames) throws Throwable {
        for (String name : detectorNames) {
            if ("*".equals(name)) {
                for (Detector detector : DetectorManager.all()) {
                    onLoadedDo(detector);
                }
            } else {
                Detector detector = DetectorManager.get(name);
                onLoadedDo(detector);
            }
        }
    }

    private void onLoadedDo(Detector detector) throws Throwable {
        if (detector != null) {
            detector.start();
            HealthChecker.addIndicator(detector.getName(), detector);
        }
    }
}
