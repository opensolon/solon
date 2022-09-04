package com.dtflys.forest.solon;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.Interceptor;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.LoadBalance;

public class UpstreamInterceptor implements Interceptor {

    public boolean beforeExecute(ForestRequest request) {
        if ("upstream".equals(request.getScheme())) {
            LoadBalance loadBalance=Bridge.upstreamFactory().create("",request.host());
            if(loadBalance==null){
                throw new RuntimeException("未找到名为："+request.host()+" 的 Upstream");
            }
            String server =loadBalance.getServer();
            if(server==null){
                throw new RuntimeException("名为："+request.host()+" 的服务不可用");
            }
            int idx = server.indexOf("://");
            request.setScheme(server.substring(0, idx));
            request.setHost(server.substring(idx + 3));
        }
        return true;
    }


}
