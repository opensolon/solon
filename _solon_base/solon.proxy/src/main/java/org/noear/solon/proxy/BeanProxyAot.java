package org.noear.solon.proxy;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;

/**
 * Aot 运行时的代理实现（为了方便识别谁有代理）
 *
 * @author noear
 * @see 2.2
 */
public class BeanProxyAot implements BeanWrap.Proxy {
    private static final BeanProxyAot global = new BeanProxyAot();

    public static BeanProxyAot getGlobal() {
        return global;
    }

    @Override
    public Object getProxy(AopContext ctx, Object bean) {
        return bean;
    }
}
