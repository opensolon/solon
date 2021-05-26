package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.service.CloudJobService;
import org.noear.solon.core.handle.Handler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 */
public class CloudJobServiceWaterImp implements CloudJobService {
    public static final CloudJobServiceWaterImp instance = new CloudJobServiceWaterImp();

    public Map<String, Handler> jobMap = new LinkedHashMap<>();

    public Handler get(String name) {
        return jobMap.get(name);
    }

    @Override
    public boolean register(String name, Handler handler) {
        jobMap.put(name, handler);
        return false;
    }

    @Override
    public boolean isRegistered(String name) {
        return jobMap.containsKey(name);
    }
}
