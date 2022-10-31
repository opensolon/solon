package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.service.CloudLockService;
import org.noear.water.utils.LockUtils;

/**
 * 分布式锁服务
 *
 * @author noear
 * @since 1.3
 */
public class CloudLockServiceWaterImp implements CloudLockService {


    @Override
    public boolean tryLock(String group, String key, int seconds, String holder) {
        if (holder == null) {
            holder = "-";
        }

        return LockUtils.tryLock(group, key, seconds, holder);
    }

    @Override
    public void unLock(String group, String key, String holder) {
        LockUtils.unLock(group, key, holder);
    }

    @Override
    public boolean isLocked(String group, String key) {
        return LockUtils.isLocked(group, key);
    }

    @Override
    public String getHolder(String group, String key) {
        return LockUtils.getLockValue(group, key);
    }
}
