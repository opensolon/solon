package org.noear.solon.extend.uapi;

import org.noear.solon.XNav;
import org.noear.solon.XUtil;
import org.noear.solon.core.*;

/**
 * UAPI导航控制器
 * */
public abstract class UApiNav extends XNav {
    private XHandler _def;

    public UApiNav() {
        super();

        register();
    }

    /**
     * 注册二级代理
     */
    protected abstract void register();


    @Override
    public void handle(XContext c) throws Throwable {
        //转换上下文
        //
        XContext c2 = context(c);
        XContextUtil.currentRemove();
        XContextUtil.currentSet(c2);

        //调用父级处理
        super.handle(c2);
    }

    /**
     * 添加接口
     */
    public void add(Object api) {
        BeanWrap bw = new BeanWrap(api.getClass(), api);
        UApiWrap uw = new UApiWrap(bw);

        uw.load(new XHandlerSlots() {
            @Override
            public void before(String expr, XMethod method, int index, XHandler handler) {
                //不支持拦截器
            }

            @Override
            public void after(String expr, XMethod method, int index, XHandler handler) {
                //不支持拦截器
            }

            @Override
            public void add(String expr, XMethod method, XHandler handler) {
                UApiAction act = (UApiAction) handler;
                if (act.mapping() != null) {
                    act.name = act.mapping().value();
                } else {
                    act.name = act.method().name();
                }

                if(XUtil.isEmpty(act.name)){
                    _def = act;
                }else {
                    addDo(act.name, act);
                }
            }
        });
    }

    /**
     * 添加接口
     */
    @Override
    public void add(String path, XHandler handler) {
        throw new RuntimeException("No supported");
    }

    @Override
    public XHandler get(XContext c, String path) {
        UApiAction api = (UApiAction) super.get(c, path);

        c.attrSet("_uapinav", "1");

        if (api == null) {
            c.setHandled(true);
            return _def;
        } else {
            c.attrSet("api", api.name);
            return api;
        }
    }

    /**
     * 提供上下文转换机制
     */
    public XContext context(XContext ctx) {
        return ctx;
    }
}
