package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.service.CloudMetricService;
import org.noear.water.WW;
import org.noear.water.WaterClient;

/**
 * 分布式计数服务
 *
 * @author noear
 * @since 1.4
 */
public class CloudMetricServiceWaterImp implements CloudMetricService {
    @Override
    public void addCount(String group, String category, String item, long val) {
        WaterClient.Track.addCount(group, category, item, val);

    }

    @Override
    public void addMeter(String group, String category, String item, long val) {
        if (WW.track_service.equals(group)) {
            WaterClient.Track.addMeterByNode(category, item, val);
        } else if (WW.track_from.equals(group)) {
            WaterClient.Track.addMeterByFrom(category, item, val);
        } else {
            WaterClient.Track.addMeter(group, category, item, val);
        }
    }

    @Override
    public void addGauge(String group, String category, String item, long val) {
        WaterClient.Track.addGauge(group, category, item, val);
    }
}
