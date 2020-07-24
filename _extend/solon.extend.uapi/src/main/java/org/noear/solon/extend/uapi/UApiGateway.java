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
public abstract class UApiGateway implements XHandler , XRender {
    private XHandler _def;
    private XNav _nav;

    public UApiGateway() {
        super();

        _nav = new XNav(this.getClass().getAnnotation(XMapping.class)){
            @Override
            protected XHandler findDo(XContext c, String path) {
                return UApiGateway.this.findDo(c, path);
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
     * */
    @Override
    public void handle(XContext c) throws Throwable {
        //转换上下文
        //
        XContext c2 = context(c);
        XContextUtil.currentRemove();
        XContextUtil.currentSet(c2);

        //调用父级处理
        _nav.handle(c2);
    }

    /**
     * for XRender (用于接管 XContext::render)
     * */
    @Override
    public void render(Object obj, XContext c) throws Throwable {
        if (obj instanceof UApiError) {
            UApiError exp = (UApiError) obj;

            Map<String, Object> map = new HashMap();
            map.put("code", exp.getCode());
            map.put("msg", exp.getMessage());
            c.renderReal(map);
        } else {
            c.renderReal(obj);
        }
    }


    /**
     * 添加前置拦截器
     * */
    public <T extends XHandler> void addBefore(Class<T> interceptorClz) {
        _nav.before(Aop.get(interceptorClz));
    }

    /**
     * 添加前置拦截器
     * */
    public  void addBefore(XHandler interceptor) {
        _nav.before(interceptor);
    }

    /**
     * 添加后置拦截器
     * */
    public <T extends XHandler> void addAfter(Class<T> interceptorClz) {
        _nav.after(Aop.get(interceptorClz));
    }

    /**
     * 添加后置拦截器
     * */
    public  void addAfter(XHandler interceptor) {
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
     * 查找接口
     * */
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
            return api;
        }
    }

    /**
     * 转换 上下文（关键的地方）
     */
    protected XContext context(XContext ctx) {
        return new UApiContext(ctx, this);
    }
}
