package org.noear.solon.extend.uapi;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.BeanWebWrap;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.XAction;

import java.lang.reflect.Method;

public class UApiBeanWebWrap extends BeanWebWrap {
    public UApiBeanWebWrap(BeanWrap wrap) {
        super(wrap);
    }

    @Override
    protected XAction createAction(BeanWrap bw, Method method, XMapping mp, String path) {
        return new UApiAction(bw, method, mp, path);
    }
}
