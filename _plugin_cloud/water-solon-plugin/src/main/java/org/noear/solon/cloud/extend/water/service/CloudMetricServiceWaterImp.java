package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudMetricService;
import org.noear.water.WaterClient;
import org.noear.water.track.TrackBuffer;

/**
 * @author noear
 * @since 1.4
 */
public class CloudMetricServiceWaterImp implements CloudMetricService {
    @Override
    public void addCount(String group, String category, String item, long num) {
        TrackBuffer.singleton().appendCount(group, category, item, num);
    }

    @Override
    public void addMeter(String group, String category, String item, long num, boolean isolated) {
        if (isolated) {
            WaterClient.Track.track(group, category, item, num);
        } else {
            String _node = Instance.local().address();
            String _from = CloudClient.trace().getFromId();

            WaterClient.Track.track(group, category, item, num, _node, _from);
        }
    }

    @Override
    public void addGauge(String group, String category, String item, Object val) {
        throw new UnsupportedOperationException();
    }
}
