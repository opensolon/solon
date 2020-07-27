package org.noear.solon.core;

import org.noear.solon.ext.Fun2;

import java.util.function.Consumer;

/**
 * Bean 订阅者
 * */
public class BeanSubscriber {
    public Fun2<Object, BeanWrap, Boolean> expr;
    public Consumer<BeanWrap> callback;

    public BeanSubscriber(Fun2<Object, BeanWrap, Boolean> expr, Consumer<BeanWrap> callback) {
        this.expr = expr;
        this.callback = callback;
    }
}
