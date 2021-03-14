package org.noear.solon.cloud.impl;

import org.noear.solon.Solon;
import org.noear.solon.cloud.model.Entry;
import org.noear.solon.cloud.service.CloudBreakerService;
import org.noear.solon.core.Props;

import java.util.HashMap;
import java.util.Map;

/**
 * 本地中断器服务
 *
 * 常用指标：
 * sbc：并发链接数，Simultaneous Browser Connections
 * qps：每秒请求数，Query Per Second
 *
 * @author noear
 * @since 1.3
 */
public class CloudBreakerServiceLocalImp implements CloudBreakerService {
    Map<String, Entry> breakers = new HashMap<>();

    public CloudBreakerServiceLocalImp() {
        Props props = Solon.cfg().getProp("solon.cloud.local.breaker");
        if (props.size() > 0) {
            for (Object k : props.keySet()) {
                if (k instanceof String) {
                    String key = (String) k;
                    int val = props.getInt(key, 0);
                    if (val > 0) {
                        breakers.put(key, new CloudEntryLocalImp(val));
                    }
                }
            }
        }
    }

    @Override
    public Entry entry(String breakerName) throws Exception {
        Entry tmp = breakers.get(breakerName);

        if (tmp == null) {
            throw new IllegalArgumentException("Missing breaker configuration: " + breakerName);
        } else {
            tmp.enter();
        }

        return tmp;
    }
}
