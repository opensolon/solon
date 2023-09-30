package org.noear.solon.health.detector.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppBeanLoadEndEvent;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.health.detector.Detector;
import org.noear.solon.health.detector.DetectorManager;
import org.noear.solon.health.detector.impl.*;

/**
 * @author noear
 * @since 1.5
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        //添加内部探测器
        DetectorManager.add(new CpuDetector());
        DetectorManager.add(new JvmMemoryDetector());
        DetectorManager.add(new OsDetector());
        DetectorManager.add(new QpsDetector());
        DetectorManager.add(new MemoryDetector());
        DetectorManager.add(new DiskDetector());

        //添加用户定义的探测器
        context.subBeansOfType(Detector.class, detector -> {
            DetectorManager.add(detector);
        });

        //健康配置器
        HealthConfigurator healthConfigurator = new HealthConfigurator();
        if (healthConfigurator.isEnabled()) {
            EventBus.subscribe(AppBeanLoadEndEvent.class, e->{
                healthConfigurator.configure();
            });
        }
    }
}
