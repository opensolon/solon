package org.noear.solon.core;

import java.util.function.Consumer;

/**
 * Bean 订阅者
 * */
public class BeanSubscriber {
    public String tag; //第1优先
    public Object label; //第2优先
    public Consumer<BeanWrap> callback;

    public BeanSubscriber(Object label, Consumer<BeanWrap> callback) {
        this.label = label;
        this.callback = callback;
        if (label instanceof String && ((String) label).startsWith("@")) {
            this.tag = ((String) label).substring(1);
        }
    }

    public boolean matched(Object key, BeanWrap wrap) {
        if (tag != null) {
            return tag.equals(wrap.tag());
        }

        if (label == null) {
            return true;
        } else {
            return label.equals(key);
        }
    }
}
