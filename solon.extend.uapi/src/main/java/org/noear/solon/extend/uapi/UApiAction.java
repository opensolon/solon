package org.noear.solon.extend.uapi;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.*;

import java.lang.reflect.Method;

public class UApiAction extends XAction {
    private String _name;

    public UApiAction(BeanWrap bw, Method m, XMapping mp, String path) {
        super(bw, m, mp, path);

        if (mp == null) {
            _name = m.getName();
        } else {
            _name = mp.value();
        }
    }

    public String name() {
        return _name;
    }

    protected void innerRender(XContext x, Object result) throws Throwable {
        if (result == null) {
            return;
        }

        //取消原有的渲染；改为属性传递
        //
        x.attrSet("result", result);
    }
}
