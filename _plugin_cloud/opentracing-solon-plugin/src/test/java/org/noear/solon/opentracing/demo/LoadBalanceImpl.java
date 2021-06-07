package org.noear.solon.opentracing.demo;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.LoadBalance;

/**
 * @author noear 2021/6/7 created
 */
@Component("hellorpc")
public class LoadBalanceImpl implements LoadBalance {
    @Override
    public String getServer() {
        return "localhost:" + Solon.global().port();
    }
}
