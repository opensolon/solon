package org.noear.solon;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.*;

import java.util.HashMap;
import java.util.Map;


/**
 * 本地网关（替代旧的XNav）
 *
 * 提供容器，重新组织处理者运行；只支持HASH路由
 *
 * 问题：如何获取主控制器实例???
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
     */
    @Override
    public void render(Object obj, XContext c) throws Throwable {
        if (c.getRendered()) {
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
        //
        //不要接管异常，因为后面没有处理了（CodeThrowable，已在handleDo处理）
        //

        XHandler m = findDo(c);
        Object bean = null;

        //m 不可能为 null；有 _def 打底
        if (m != null) {
            //预加载控制器，确保所有的处理者可以都可以获取控制器
            if (m instanceof XAction) {
                bean = ((XAction) m).bean().get();
                c.attrSet("controller", bean);
            }

            //前置处理
            for (XHandler h : _before) {
                handleDo(c, h, XEndpoint.before, null);
            }

            //主处理
            if (c.getHandled() == false) {
                handleDo(c, m, XEndpoint.main, bean);
            } else {
                render(c.result, c);
            }

            //后置处理（确保不受前面的异常影响）
            for (XHandler h : _after) {
                handleDo(c, h, XEndpoint.after, null);
            }
        }
    }

    /**
     * 主要对DataThrowable进行处理
     */
    protected void handleDo(XContext c, XHandler h, int endpoint, Object bean) throws Throwable {
        if (endpoint != XEndpoint.after) {
            //
            //确保非后置处理不出错，出错转为UapiCode（前置处理，也可以填接抛出数据）
            //
            try {
                if (bean == null) {
                    h.handle(c);
                } else {
                    ((XAction) h).invoke(c, bean);
                }
            } catch (DataThrowable ex) {
                c.setHandled(true); //停止处理

                render(ex, c);
            } catch (Throwable ex) {
                c.setHandled(true); //停止处理

                c.attrSet("error", ex);
                render(ex, c);
                XMonitor.sendError(c, ex);
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
            if (h instanceof XAction) {
                XAction h2 = (XAction) h;

                if (XUtil.isEmpty(h2.name())) {
                    _def = h2;
                } else {
                    add(h2.name(), h2);
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

    /**
     * 查找接口
     */
    protected XHandler findDo(XContext c) throws Throwable {
        XHandler h = _main.get(c.pathAsUpper());

        if (h == null) {
            _def.handle(c);
            c.setHandled(true);
            return _def;
        } else {
            if (h instanceof XAction) {
                c.attrSet("handler_name", ((XAction) h).name());
            }
            return h;
        }
    }
}
