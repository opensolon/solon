package org.noear.solon.extend.uapi;

import org.noear.solon.core.*;

import java.lang.reflect.Method;

public class XCallable implements XHandler {
    private MethodWrap cWrap = null;
    private BeanWrap bWrap = null;

    public XCallable() {
        if (bWrap == null) {
            bWrap = new BeanWrap(this.getClass(), this);

            for (Method m : this.getClass().getDeclaredMethods()) {
                if (m.getName().equals("call")) {
                    cWrap = MethodWrap.get(m);
                    break;
                }
            }
        }
    }

    @Override
    public void handle(XContext x) throws Throwable {
        if (x.getHandled() == false) {
            try {
                if (cWrap == null) {
                    innerRender(x, null);
                } else {
                    innerRender(x, innerCall(x));
                }
            } catch (Throwable ex) {
                x.attrSet("error", ex);
                innerRender(x, ex);
                XMonitor.sendError(x, ex);
            }
        }
    }

    protected Object innerCall(XContext x) throws Throwable {
        return XActionUtil.exeMethod(bWrap.get(), cWrap, x);
    }

    protected void innerRender(XContext x, Object result) throws Throwable {
        x.render(result);
    }
}
