package org.noear.solon.cloud.extend.local.service;

import org.noear.solon.cloud.service.CloudListService;

/**
 * 云端名单（本地摸拟实现）
 *
 * @author noear
 * @since 1.11
 */
public class CloudListServiceLocalImpl implements CloudListService {
    @Override
    public boolean inList(String names, String type, String value) {
        return false;
    }
}
