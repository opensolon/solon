package org.noear.solon.cloud.service;

import java.util.HashSet;
import java.util.Set;

/**
 * @author noear
 * @since 2.4
 */
public class CloudMetricServiceManager implements CloudMetricService{
    Set<CloudMetricService> services = new HashSet<>();

    public int size(){
        return services.size();
    }

    public void register(CloudMetricService service){
        services.add(service);
    }

    @Override
    public void addCounter(String group, String category, String item, long increment) {
        for(CloudMetricService s : services){
            s.addCounter(group, category, item, increment);
        }
    }

    @Override
    public void addTimer(String group, String category, String item, long record) {
        for(CloudMetricService s : services){
            s.addTimer(group, category, item, record);
        }
    }

    @Override
    public void addGauge(String group, String category, String item, long number) {
        for(CloudMetricService s : services){
            s.addGauge(group, category, item, number);
        }
    }
}
