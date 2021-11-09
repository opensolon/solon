package org.noear.solon.extend.health.detector.integration;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.health.HealthChecker;
import org.noear.solon.extend.health.HealthIndicator;

/**
 * @author noear
 * @since 1.5
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        String detectorNamesStr = Solon.cfg().get("solon.Health.detector");
        if (Utils.isEmpty(detectorNamesStr)) {
            return;
        }

        HealthIndicator indicator = new DetectorHealthIndicator(detectorNamesStr);
        HealthChecker.addIndicator("machine", indicator);
    }
}
