package org.noear.solon.extend.consul;

import org.noear.solon.core.LoadBalance;

import java.util.HashSet;
import java.util.Iterator;

/**
 * 负载平衡
 *
 * @author 夜の孤城
 * @since 1.2
 */
class LoadBalanceSimple implements LoadBalance {
    HashSet<String> servers = new HashSet<>();
    Iterator<String> tmp = servers.iterator();

    /**
     * 获取 server
     * */
    @Override
    public synchronized String getServer() {
        if (!tmp.hasNext()) {
            tmp = servers.iterator();
        }

        return tmp.next();
    }

    /**
     * 添加 server
     * */
    protected void addServer(String server) {
        servers.add(server);
    }
}
