package org.noear.solon.extend.uapi;

import org.noear.solon.XNav;
import org.noear.solon.XUtil;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.*;

import java.lang.reflect.Method;

/**
 * UAPI导航控制器
 *
 * 1.提供容器，重新组织Handler运行
 * */
public abstract class UApiGateway extends XNav {
    private XHandler _def;

    public UApiGateway() {
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

    public <T extends XHandler > void before(Class<T> clz){
        super.before(Aop.get(clz));
    }

    public <T extends XHandler > void after(Class<T> clz){
        super.after(Aop.get(clz));
    }

    /**
     * 添加接口
     */
    public void add(Class<?> clz, boolean remoting) {
        if (clz != null) {
            BeanWrap bw = Aop.wrap(clz);
            bw.remotingSet(remoting);

            add(bw);
        }
    }

    public void add(Class<?> clz){
        if (clz != null) {
            add(Aop.wrap(clz));
        }
    }

    /**
     * 添加接口
     */
    public void add(BeanWrap bw) {
        if(bw == null){
            return;
        }

        BeanWebWrap uw = new BeanWebWrap(bw, mapping()){
            @Override
            protected XAction action(BeanWrap bw, Method method, XMapping mp, String path) {
                return createAction(bw, method, mp, path);
            }
        };

        uw.load((path, m, h) -> {
            UApi api = null;
            if (h instanceof UApi) {
                api = (UApi) h;
            } else {
                api = createHandler(path, h);
            }

            if (XUtil.isEmpty(api.name())) {
                _def = api;
            } else {
                addDo(api.name(), api);
            }
        });
    }

    /**
     * 添加接口
     */
    @Override
    public void add(String name, XHandler handler) {
        UApi api = createHandler(name, handler);
        add(api.name(), api);
    }

    @Override
    public XHandler get(XContext c, String path) {
        UApi api = (UApi) super.get(c, path);

        if (api == null) {
            if (_def != null) {
                try {
                    _def.handle(c);
                } catch (Throwable ex) {
                    throw new RuntimeException(ex);
                }
            }
            c.setHandled(true);
            return _def;
        } else {
            c.attrSet("uapi", api.name());
            return api;
        }
    }

    /**
     * 转换 上下文
     */
    protected XContext context(XContext ctx) {
        return new UApiContext(ctx);
    }

    /**
     * 创建 uapi action （可重载）
     * */
    protected UApiAction createAction(BeanWrap bw, Method method, XMapping mp, String path){
        return new UApiAction(bw, method, mp, path);
    }

    /**
     * 创建 uapi handler （可重载）
     * */
    protected UApiHandler createHandler(String path, XHandler handler){
        return new UApiHandler(path, handler);
    }
}
