package org.noear.solon.cloud.extend.local.service;

import org.noear.solon.cloud.service.CloudMetricService;

/**
 * 云端度量（本地摸拟实现）
 *
 * @author noear
 * @since 1.10
 */
public class CloudMetricServiceLocalImpl implements CloudMetricService {
    @Override
    public void addCount(String group, String category, String item, long val) {

    }

    @Override
    public void addMeter(String group, String category, String item, long val) {

    }

    @Override
    public void addGauge(String group, String category, String item, long val) {

    }
}
