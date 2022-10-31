package org.noear.solon.cloud.extend.snowflake.service;

import org.noear.solon.cloud.service.CloudIdService;
import org.noear.solon.cloud.service.CloudIdServiceFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
public class CloudIdServiceFactoryImp implements CloudIdServiceFactory {
    long idStart;

    public CloudIdServiceFactoryImp(long idStart) {
        this.idStart = idStart;
    }


    private Map<String, CloudIdService> cached = new HashMap<>();

    @Override
    public CloudIdService create(String group, String service) {
        String block = group + "_" + service;
        CloudIdService tmp = cached.get(block);

        if (tmp == null) {
            synchronized (block.intern()) {
                tmp = cached.get(block);
                if (tmp == null) {
                    tmp = new CloudIdServiceImp(block, idStart);
                    cached.put(block, tmp);
                }
            }
        }

        return tmp;
    }
}
