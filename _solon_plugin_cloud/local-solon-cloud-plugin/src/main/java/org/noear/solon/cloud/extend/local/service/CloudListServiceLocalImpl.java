package org.noear.solon.cloud.extend.local.service;

import org.noear.solon.cloud.service.CloudListService;

/**
 * @author noear
 * @since 1.10
 */
public class CloudListServiceLocalImpl implements CloudListService {
    @Override
    public boolean inList(String names, String type, String value) {
        return false;
    }
}
