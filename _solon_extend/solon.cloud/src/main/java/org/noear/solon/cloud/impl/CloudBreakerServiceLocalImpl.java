package org.noear.solon.cloud.impl;

import org.noear.solon.Solon;
import org.noear.solon.cloud.model.BreakerEntrySim;
import org.noear.solon.cloud.model.BreakerException;
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
    static final String CONFIG_DEF = "root";

    private Map<String, BreakerEntrySim> breakers = new HashMap<>();
    private int rootValue = 0;

    public CloudBreakerServiceLocalImpl() {
        Props props = Solon.cfg().getProp(CONFIG_PREFIX);

        if (props.size() > 0) {
            //默认值
            rootValue = props.getInt(CONFIG_DEF,0);

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
        BreakerEntrySim tmp = breakers.get(breakerName);

        if(tmp == null && rootValue > 0) {
            //动态创建
            synchronized (breakerName.intern()) {
                tmp = breakers.get(breakerName);

                if (tmp == null) {
                    tmp = create(breakerName, rootValue);
                    breakers.put(breakerName, tmp);
                }
            }
        }

        if (tmp == null) {
            throw new IllegalArgumentException("Missing breaker configuration: " + breakerName);
        } else {
            return tmp.enter();
        }
    }
}
