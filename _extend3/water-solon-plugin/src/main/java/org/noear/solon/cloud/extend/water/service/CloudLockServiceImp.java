package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.service.CloudLockService;
import org.noear.water.utils.LockUtils;

/**
 * @author noear
 * @since 1.3
 */
public class CloudLockServiceImp implements CloudLockService {
    @Override
    public boolean lock(String group, String key, int seconds) {
        return LockUtils.tryLock(group, key, seconds);
    }

    @Override
    public void unlock(String group, String key) {
        LockUtils.unLock(group, key);
    }

    @Override
    public boolean isLocked(String group, String key) {
        return LockUtils.isLocked(group, key);
    }
}
