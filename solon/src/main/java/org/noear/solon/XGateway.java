package org.noear.solon;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.*;

import java.util.HashMap;
import java.util.Map;


/**
 * 本地网关（替代旧的XNav）
 *
 * 提供容器，重新组织处理者运行；只支持HASH路由
 * */
public abstract class XGateway extends XHandlerAide implements XRender {
    private XHandler _def;
    private final Map<String, XHandler> _main = new HashMap<>();
    private final String _path;
    private XMapping _mapping;

    public XGateway() {
        super();
        _mapping = this.getClass().getAnnotation(XMapping.class);
        if (_mapping == null) {
            throw new RuntimeException("No XMapping!");
        }

        _path = _mapping.value();

        //默认为404错误输出
        _def = (c) -> c.status(404);

        register();
    }

    /**
     * 注册相关接口与拦截器
     */
    protected abstract void register();

    /**
     * 允许 Action Mapping 申明
     */
    protected boolean allowActionMapping() {
        return true;
    }


    /**
     * for XRender
     * */
    @Override
    public void render(Object obj, XContext c) throws Throwable {
        if(c.getRendered()){
            return;
        }

        c.setRendered(true);

        c.result = obj;
        c.render(obj);
    }

    /**
     * for XHandler
     */
    @Override
    public void handle(XContext c) throws Throwable {

        //不要接管异常，因为后面没有处理了（CodeThrowable，已在handleDo处理）
        handle0(c);
    }

    protected void handle0(XContext c) throws Throwable {
        XHandler m = findDo(c);

        if (m != null) {
            //预加载控制器，确保所有的处理者可以都可以获取控制器
            if(m instanceof XAction){
                ((XAction) m).preload(c);
            }

            //前置处理
            for (XHandler h : _before) {
                handleDo(c, h, XEndpoint.before);
            }

            //主处理
            if (c.getHandled() == false) {
                handleDo(c, m, XEndpoint.main);
            } else {
                render(c.result, c);
            }

            //后置处理
            for (XHandler h : _after) {
                handleDo(c, h, XEndpoint.after);
            }
        } else {
            _def.handle(c);
        }
    }


    /**
     * 添加前置拦截器
     */
    public <T extends XHandler> void before(Class<T> interceptorClz) {
        super.before(Aop.get(interceptorClz));
    }


    /**
     * 添加后置拦截器
     */
    public <T extends XHandler> void after(Class<T> interceptorClz) {
        super.after(Aop.get(interceptorClz));
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

        BeanWebWrap uw = new BeanWebWrap(beanWp, _path, remoting, this, allowActionMapping());

        uw.load((path, m, h) -> {
            XAction api = null;
            if (h instanceof XAction) {
                api = (XAction) h;

                if (XUtil.isEmpty(api.name())) {
                    _def = api;
                } else {
                    add(api.name(), api);
                }
            }
        });
    }

    /**
     * 添加二级路径处理
     */
    public void add(String path, XHandler handler) {
        addDo(path, handler);
    }

    protected void addDo(String path, XHandler handler) {
        //addPath 已处理 path1= null 的情况
        _main.put(XUtil.mergePath(_path, path).toUpperCase(), handler);
    }

    //
    //
    //

    /**
     * 接管XNav的handleDo（主要对DataThrowable进行处理）
     */
    protected void handleDo(XContext c, XHandler h, int endpoint) throws Throwable {
        if (endpoint != XEndpoint.after) {
            //
            //确保非后置处理不出错，出错转为UapiCode（前置处理，也可以填接抛出数据）
            //
            try {
                h.handle(c);
            } catch (XResultCode ex) {
                c.setHandled(true);
                render(ex, c);
            }
            //
            //别的异常不管，输出50X错误
            //
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
    protected XHandler findDo(XContext c) {
        XHandler api = _main.get(c.pathAsUpper());

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
                c.attrSet("handler_name", ((XAction) api).name());
            }

            return api;
        }
    }
}
