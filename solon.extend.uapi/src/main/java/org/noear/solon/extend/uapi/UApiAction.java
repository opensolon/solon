package org.noear.solon.extend.uapi;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.*;

import java.lang.reflect.Method;

public class UApiAction extends XAction {
    private String _name;

    public UApiAction(BeanWrap bw, Method method, XMapping mp, String path) {
        super(bw,method,mp,path);

        if (mp == null) {
            _name = method.getName();
        } else {
            _name = mp.value();
        }
    }

    public String name(){
        return _name;
    }

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
