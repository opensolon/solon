package org.noear.solon.core.util;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.runtime.NativeDetector;

/**
 * @author noear
 * @since 2.5
 */
public class ProxyBinder {

    private static ProxyBinder global;
    static {
        //（静态扩展约定：org.noear.solon.extend.impl.XxxxExt）
        ProxyBinder ext = ClassUtil.tryInstance("org.noear.solon.extend.impl.ProxyBinderExt");

        if (ext == null) {
            global = new ProxyBinder();
        } else {
            global = ext;
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
