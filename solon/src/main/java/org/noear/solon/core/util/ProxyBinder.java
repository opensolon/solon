package org.noear.solon.core.util;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.runtime.NativeDetector;

/**
 * 代理绑定器
 *
 * @author noear
 * @since 2.5
 */
public class ProxyBinder {

    private static ProxyBinder global;
    static {
        //（静态扩展约定：org.noear.solon.extend.impl.XxxxExt）
        global = ClassUtil.tryInstance("org.noear.solon.extend.impl.ProxyBinderExt");

        if (global == null) {
            global = new ProxyBinder();
        }
    }

    public static ProxyBinder global() {
        return global;
    }

    /**
     * 绑定代理
     * */
    public void binding(BeanWrap bw){
        if (NativeDetector.isNotAotRuntime()) {
            throw new IllegalStateException("Missing plugin dependency: 'solon.proxy'");
        }
    }
}
