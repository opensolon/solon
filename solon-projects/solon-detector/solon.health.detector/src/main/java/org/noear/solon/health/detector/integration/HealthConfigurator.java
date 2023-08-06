package org.noear.solon.health.detector.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.health.HealthChecker;
import org.noear.solon.health.detector.Detector;
import org.noear.solon.health.detector.DetectorManager;

import java.util.Map;

/**
 * 健康处理器
 *
 * @author noear
 * @since 2.4
 */
public class HealthConfigurator {
    static final String HEALTH_DETECTOR = "solon.health.detector";

    String detectorNamesStr;

    public HealthConfigurator() {
        detectorNamesStr = Solon.cfg().get(HEALTH_DETECTOR);
    }


    /**
     * 是否启用
     */
    public boolean isEnabled() {
        return Utils.isNotEmpty(detectorNamesStr);
    }

    /**
     * 配置
     */
    public void configure() throws Throwable {
        Map<String, Detector> tmp = DetectorManager.getMore(detectorNamesStr.split(","));

        for (Map.Entry<String, Detector> kv : tmp.entrySet()) {
            kv.getValue().start();
            HealthChecker.addIndicator(kv.getKey(), kv.getValue());
        }
    }
}
