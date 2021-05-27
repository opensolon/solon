package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudMetricService;
import org.noear.water.WaterClient;
import org.noear.water.track.TrackBuffer;

import java.util.Map;

/**
 * @author noear
 * @since 1.4
 */
public class CloudMetricServiceWaterImp implements CloudMetricService {
    @Override
    public void addCount(String group, String name, int count) {
        TrackBuffer.singleton().appendCount(Solon.cfg().appName(), group, name, count);
    }

    @Override
    public void addMeter(String group, String name, long interval, boolean isolated) {
        if (isolated) {
            WaterClient.Track.track(Solon.cfg().appName(), group, name, interval);
        } else {
            Instance node = Instance.local();
            String fromId = CloudClient.trace().getFromId();

            WaterClient.Track.track(node.service(), group, name, interval, node.address(), fromId);
        }
    }

    @Override
    public void addGauge(String group, String name, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addLog(String group, String name, Map<String, Object> data) {
        throw new UnsupportedOperationException();
    }
}
