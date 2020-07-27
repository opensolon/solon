package org.noear.solon.core;

import java.util.function.Consumer;

/**
 * Bean 订阅者
 * */
public class BeanSubscriber {
    public Object key;
    public String tag;
    public Consumer<BeanWrap> callback;

    public BeanSubscriber(Consumer<BeanWrap> callback) {
        this.callback = callback;
    }

    public BeanSubscriber(Object key, Consumer<BeanWrap> callback) {
        //
        //key = [tag, key, type]
        //
        if (key instanceof String && ((String) key).startsWith("@")) {
            //tag
            this.tag = ((String) key).substring(1);
        } else {
            //key
            this.key = key;
        }

        this.callback = callback;
    }
}
