package org.noear.solon.core.handle;

import org.noear.solon.core.BeanWrap;

/**
 * 通用处理接口加载器工厂
 *
 * @author noear
 * @since 2.4
 */
public class HandlerLoaderFactory {
    static HandlerLoaderFactory global = new HandlerLoaderFactory();
    public static HandlerLoaderFactory global(){
        return global;
    }
    public static void globalSet(HandlerLoaderFactory instance) {
        if (instance != null) {
            //可以通过替换 HandlerLoaderFactory，实现控制器解析的重写支持
            global = instance;
        }
    }

    public HandlerLoader create(BeanWrap wrap) {
        return new HandlerLoader(wrap);
    }

    public HandlerLoader create(BeanWrap wrap, String mapping) {
        return create(wrap, mapping, wrap.remoting(), null, true);
    }

    public HandlerLoader create(BeanWrap wrap, String mapping, boolean remoting) {
        return create(wrap, mapping, remoting, null, true);
    }

    public HandlerLoader create(BeanWrap wrap, String mapping, boolean remoting, Render render, boolean allowMapping) {
        return new HandlerLoader(wrap, mapping, remoting, render, allowMapping);
    }
}
