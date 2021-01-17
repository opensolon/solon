package org.noear.solon.extend.consul;

import org.noear.solon.core.LoadBalance;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 负载平衡工厂
 *
 * @author 夜の孤城
 * @since 1.2
 * */
class LoadBalanceSimpleFactory implements LoadBalance.Factory {
    private Map<String, LoadBalanceSimple> storage = new HashMap<>();

    /**
     * 更新仓库
     */
    protected void update(Map<String, LoadBalanceSimple> storage) {
        if (storage != null) {
            this.storage = storage;
        }
    }

    /**
     * 获取
     */
    protected LoadBalanceSimple get(String service) {
        return storage.get(service);
    }


    /**
     * 生成负载平衡
     */
    @Override
    public LoadBalance create(String service) {
        return new LoadBalanceProxy(service);
    }

    class LoadBalanceProxy implements LoadBalance {
        String service;

        private LoadBalanceProxy(String service) {
            this.service = service;
        }

        public synchronized String getServer() {
            LoadBalance real = get(service);

            if (real == null) {
                return null;
            } else {
                return real.getServer();
            }
        }
    }
}
