package org.noear.solon.extend.uapi;

import org.noear.solon.XNav;
import org.noear.solon.XUtil;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.*;

import java.util.HashMap;
import java.util.Map;


/**
 * UAPI网关
 *
 * 提供容器，重新组织XAction运行
 * */
public abstract class UapiGateway implements XHandler , XRender {
    private XHandler _def;
    private XNav _nav;

    public UapiGateway() {
        super();

        _nav = new XNav(this.getClass().getAnnotation(XMapping.class)) {
            @Override
            protected XHandler findDo(XContext c, String path) {
                return UapiGateway.this.findDo(c, path);
            }

            @Override
            protected void handleDo(XContext c, XHandler h, int endpoint) throws Throwable {
                super.handleDo(c, h, endpoint);
            }
        };

        _def = (c) -> c.status(404);

        register();
    }

    /**
     * 注册相关接口与拦截器
     */
    protected abstract void register();


    /**
     * for XHandler
     */
    @Override
    public void handle(XContext c) throws Throwable {
        //转换上下文
        //
        XContext c2 = context(c);
        XContextUtil.currentRemove();
        XContextUtil.currentSet(c2);

        //不要接管异常，因为后面没有处理了（DataThrowable，已在handleDo处理）
        _nav.handle(c2);
    }

    /**
     * for XRender (用于接管 XContext::render)
     *
     * 主要为了子类可重写
     */
    @Override
    public void render(Object obj, XContext c) throws Throwable {
        c.renderReal(obj);
    }


    /**
     * 添加前置拦截器
     */
    public <T extends XHandler> void addBefore(Class<T> interceptorClz) {
        _nav.before(Aop.get(interceptorClz));
    }

    /**
     * 添加前置拦截器
     */
    public void addBefore(XHandler interceptor) {
        _nav.before(interceptor);
    }

    /**
     * 添加后置拦截器
     */
    public <T extends XHandler> void addAfter(Class<T> interceptorClz) {
        _nav.after(Aop.get(interceptorClz));
    }

    /**
     * 添加后置拦截器
     */
    public void addAfter(XHandler interceptor) {
        _nav.after(interceptor);
    }

    /**
     * 添加接口
     */
    public void add(Class<?> beanClz) {
        if (beanClz != null) {
            BeanWrap bw = Aop.wrap(beanClz);

            add(bw, bw.remoting());
        }
    }

    /**
     * 添加接口（remoting ? 采用@json进行渲染）
     */
    public void add(Class<?> beanClz, boolean remoting) {
        if (beanClz != null) {
            add(Aop.wrap(beanClz), remoting);
        }
    }

    public void add(BeanWrap beanWp) {
        add(beanWp, beanWp.remoting());
    }

    /**
     * 添加接口（适用于，从Aop工厂遍历加入；或者把rpc代理包装成bw）
     */
    public void add(BeanWrap beanWp, boolean remoting) {
        if (beanWp == null) {
            return;
        }

        BeanWebWrap uw = new BeanWebWrap(beanWp, _nav.mapping(), remoting);

        uw.load((path, m, h) -> {
            XAction api = null;
            if (h instanceof XAction) {
                api = (XAction) h;

                if (XUtil.isEmpty(api.name())) {
                    _def = api;
                } else {
                    _nav.add(api.name(), api);
                }
            }
        });
    }

    public void add(String path, XHandler handler) {
        _nav.add(path, handler);
    }

    /**
     * 执行接口（主要对DataThrowable进行处理）
     */
    protected void handleDo(XContext c, XHandler h, int endpoint) throws Throwable {
        if (endpoint != XEndpoint.after) {
            //
            //确保非后置处理不出错，出错转为UapiCode（前置处理，也可以填接抛出数据）
            //
            try {
                h.handle(c);
            } catch (DataThrowable ex) {
                render(ex, c);
            } catch (Throwable ex) {
                //其它异常做为未知代码
                //
                render(new UapiCode(ex), c);
            }
        } else {
            //
            //后置处理，不能再抛数据了（不然，没完没了）
            //
            h.handle(c);
        }
    }

    /**
     * 查找接口
     */
    protected XHandler findDo(XContext c, String path) {
        XHandler api = _nav.get(path);

        if (api == null) {
            //主要增加默认接口支持
            //
            if (_def != null) {
                try {
                    _def.handle(c);
                } catch (Throwable ex) {
                    throw new RuntimeException(ex);
                }
            }
            //中止执行
            c.setHandled(true);
            return _def;
        } else {
            if (api instanceof XAction) {
                c.attrSet("api", ((XAction) api).name());
            }

            return api;
        }
    }

    /**
     * 转换 上下文（关键的地方）
     */
    protected XContext context(XContext ctx) {
        return new UapiContext(ctx, this);
    }
}
