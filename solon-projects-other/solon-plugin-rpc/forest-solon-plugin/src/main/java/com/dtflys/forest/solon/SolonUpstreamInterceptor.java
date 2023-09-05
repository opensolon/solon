package com.dtflys.forest.solon;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.Interceptor;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.LoadBalance;

/**
 * ForestClient 拦截器，用于支持 upstream 协议
 *
 * @author 夜の孤城
 * @since 1.10
 */
public class SolonUpstreamInterceptor implements Interceptor {

    @Inject
    AppContext aopContext;

    @Override
    public boolean beforeExecute(ForestRequest request) {
        if ("upstream".equals(request.getScheme())) {
            //尝试从工厂获取
            LoadBalance loadBalance = Bridge.upstreamFactory().create("", request.host());

            if (loadBalance == null) {
                //尝试从容器获取
                loadBalance = aopContext.getBean(request.host());
            }

            if (loadBalance == null) {
                throw new IllegalStateException("ForestClient: Not found upstream: " + request.host());
            }

            //尝试获取 server
            String server = loadBalance.getServer();
            if (server == null) {
                throw new IllegalStateException("NamiClient: Upstream(" + request.host() + ") not found server!");
            }

            int idx = server.indexOf("://");
            request.setScheme(server.substring(0, idx));
            request.setHost(server.substring(idx + 3));
        }

        return true;
    }
}
