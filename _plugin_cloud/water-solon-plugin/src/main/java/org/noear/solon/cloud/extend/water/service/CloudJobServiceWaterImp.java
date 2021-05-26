package org.noear.solon.cloud.extend.water.service;

import com.oracle.tools.packager.Log;
import org.noear.solon.cloud.service.CloudJobService;
import org.noear.solon.core.handle.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 */
public class CloudJobServiceWaterImp implements CloudJobService {
    private static final Logger log = LoggerFactory.getLogger(CloudJobServiceWaterImp.class);
    public static final CloudJobServiceWaterImp instance = new CloudJobServiceWaterImp();

    public Map<String, Handler> jobMap = new LinkedHashMap<>();

    public Handler get(String name) {
        return jobMap.get(name);
    }

    @Override
    public boolean register(String name, Handler handler) {
        jobMap.put(name, handler);
        log.info("CloudJob register success, name:{}, handler:{}", name, handler.getClass());
        return true;
    }

    @Override
    public boolean isRegistered(String name) {
        return jobMap.containsKey(name);
    }
}
