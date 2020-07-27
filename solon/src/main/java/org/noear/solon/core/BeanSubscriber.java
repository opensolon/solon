package org.noear.solon.core;

import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BeanSubscriber that = (BeanSubscriber) o;
        return  Objects.equals(label, that.label) &&
                Objects.equals(callback, that.callback);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, callback);
    }
}
