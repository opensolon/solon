package org.noear.solon.cloud.impl;

import org.noear.solon.Solon;
import org.noear.solon.cloud.model.BreakerEntrySim;
import org.noear.solon.cloud.model.BreakerException;
import org.noear.solon.cloud.model.BreakerEntry;
import org.noear.solon.cloud.service.CloudBreakerService;
import org.noear.solon.core.Props;

import java.util.HashMap;
import java.util.Map;

/**
 * 本地熔断服务
 *
 * 常用指标：
 * sbc：并发链接数，Simultaneous Browser Connections
 * qps：每秒请求数，Query Per Second
 *
 * @author noear
 * @since 1.3
 */
public abstract class CloudBreakerServiceLocalImpl implements CloudBreakerService {
    static final String CONFIG_PREFIX = "solon.cloud.local.breaker";

    Map<String, BreakerEntrySim> breakers = new HashMap<>();

    public CloudBreakerServiceLocalImpl() {
        Props props = Solon.cfg().getProp(CONFIG_PREFIX);

        if (props.size() > 0) {
            //初始化
            //
            for (Object k : props.keySet()) {
                if (k instanceof String) {
                    String key = (String) k;
                    int val = props.getInt(key, 0);
                    if (val > 0) {
                        breakers.put(key, create(key, val));
                    }
                }
            }

            //增加配置变化监听
            //
            Solon.cfg().onChange((key, val) -> {
                if (key.startsWith(CONFIG_PREFIX)) {
                    String name = key.substring(CONFIG_PREFIX.length() + 1);
                    BreakerEntrySim tmp = breakers.get(name);
                    if (tmp != null) {
                        tmp.reset(Integer.parseInt(val));
                    }
                }
            });
        }
    }

    protected abstract BreakerEntrySim create(String name, int value);

    @Override
    public AutoCloseable entry(String breakerName) throws BreakerException {
        BreakerEntry tmp = breakers.get(breakerName);

        if (tmp == null) {
            throw new IllegalArgumentException("Missing breaker configuration: " + breakerName);
        } else {
            return tmp.enter();
        }
    }
}
