package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.service.CloudListService;
import org.noear.water.WaterClient;

/**
 * @author noear
 * @since 1.3
 */
public class CloudListServiceImp implements CloudListService {
    @Override
    public boolean inList(String name, String type, String value) {
        return WaterClient.Whitelist.exists(name, type, value);
    }
}
