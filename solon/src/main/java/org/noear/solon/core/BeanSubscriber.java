package org.noear.solon.core;

import java.util.Objects;
import java.util.function.Consumer;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BeanSubscriber that = (BeanSubscriber) o;
        return Objects.equals(key, that.key) &&
                Objects.equals(tag, that.tag) &&
                Objects.equals(callback, that.callback);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, tag, callback);
    }
}
