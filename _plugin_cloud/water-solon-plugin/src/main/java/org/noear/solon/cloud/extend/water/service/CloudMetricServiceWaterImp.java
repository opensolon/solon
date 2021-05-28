package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudMetricService;
import org.noear.water.WW;
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
    public void addMeter(String group, String category, String item, long num) {
        if (WW.track_service.equals(group)) {
            WaterClient.Track.trackNode(category, item, num);
        } else if (WW.track_from.equals(group)) {
            WaterClient.Track.trackFrom(category, item, num);
        } else {
            WaterClient.Track.track(group, category, item, num);
        }
    }

    @Override
    public void addGauge(String group, String category, String item, Object val) {
        throw new UnsupportedOperationException();
    }
}
