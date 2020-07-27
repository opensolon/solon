package org.noear.solon.core;

import java.util.function.Consumer;

/**
 * Bean 订阅者
 * */
public class BeanSubscriber {
    public Object key; //第2优先
    public Consumer<BeanWrap> callback;

    public BeanSubscriber(Object key, Consumer<BeanWrap> callback) {
        this.key = key;
        this.callback = callback;
    }
}
