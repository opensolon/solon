package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.service.CloudListService;
import org.noear.water.WaterClient;

/**
 * 名单服务
 *
 * @author noear
 * @since 1.3
 */
public class CloudListServiceWaterImp implements CloudListService {
    @Override
    public boolean inList(String name, String type, String value) {
        return WaterClient.Whitelist.exists(name, type, value);
    }
}
