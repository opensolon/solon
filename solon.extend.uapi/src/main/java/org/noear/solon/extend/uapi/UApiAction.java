package org.noear.solon.extend.uapi;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.*;

import java.lang.reflect.Method;

public class UApiAction extends XAction {
    private String _name;

    private BeanWrap _bw;//
    private MethodWrap _mw;


    public UApiAction(BeanWrap bw, Method method, XMapping mp) {
        _bw = bw;
        _mw = MethodWrap.get(method);

        if (mp == null) {
            _name = method.getName();
        } else {
            _name = mp.value();
        }
    }

    public String name(){
        return _name;
    }

    @Override
    public void handle(XContext x) throws Throwable {
        x.remotingSet(_bw.remoting());

        try {
            do_handle(x);
        } catch (Throwable ex) {
            x.attrSet("error", ex);
            x.render(ex);
            XMonitor.sendError(x, ex);
        }
    }

    private void do_handle(XContext x) throws Throwable {
        //前置处理
        for (XHandler h : _before) {
            h.handle(x);
        }

        if (x.getHandled() == false) {
            try {
                innerRender(x, innerCall(x));
            } catch (Throwable ex) {
                x.attrSet("error", ex);
                innerRender(x, ex);
                XMonitor.sendError(x, ex);
            }
        }

        //后置处理
        for (XHandler h : _after) {
            h.handle(x);
        }
    }

    protected Object innerCall(XContext x) throws Throwable{
        return XActionUtil.exeMethod(_bw.get(), _mw, x);
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
