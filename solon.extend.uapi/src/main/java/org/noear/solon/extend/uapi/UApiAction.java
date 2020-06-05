package org.noear.solon.extend.uapi;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.XAction;
import org.noear.solon.core.XContext;

import java.lang.reflect.Method;

public class UApiAction extends XAction {
    protected String name;

    public UApiAction(BeanWrap beanWrap, Method method, XMapping mp, String path) {
        super(beanWrap, method, mp, path);
    }

    @Override
    protected void innerRender(XContext x, Object result) throws Throwable {
        if(result == null){
            return;
        }

        //_uapinav 由 UApiNav 写入
        //
        if(x.attr("_uapinav",null) == null){
            x.render(result);
        }else {
            x.attrSet("result", result);
        }
    }
}
