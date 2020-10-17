package org.noear.solon;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XNote;
import org.noear.solon.core.*;
import org.noear.solon.ext.RunnableEx;
import org.noear.solon.ext.DataThrowable;

import java.util.HashMap;
import java.util.Map;


/**
 * 本地网关
 * 提供容器，重新组织处理者运行；只支持HASH路由
 *
 * <pre><code>
 * @XMapping("/*")
 * @XController
 * public class ApiGateway extends XGateway {
 *     @Override
 *     protected void register() {
 *         before(StartHandler.class);   //添加前置拦截器，开始计时+记录请求日志
 *         before(IpHandler.class);      //添加前置拦截器，检查IP白名单
 *
 *         after(EndHandler.class);      //添加后置拦截器，结束计时+记录输出日志+记录接口性能
 *
 *         add(DemoService.class, true); //添加接口
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
public abstract class XGateway extends XHandlerAide implements XHandler, XRender {
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
    @XNote("注册相关接口与拦截器")
    protected abstract void register();

    /**
     * 允许 Action Mapping 申明
     */
    @XNote("允许 Action Mapping 申明")
    protected boolean allowActionMapping() {
        return true;
    }

    /**
     * 充许提前准备控制器
     * */
    @XNote("充许提前准备控制器")
    protected boolean allowReadyController(){return true;}

    /**
     * 充许路径合并
     * */
    @XNote("充许路径合并")
    protected boolean allowPathMerging(){return true;}


    /**
     * for XRender （用于接管 BeanWebWrap 和 XAction 的渲染）
     */
    @Override
    public void render(Object obj, XContext c) throws Throwable {
        if (c.getRendered()) {
            return;
        }

        //最多一次渲染
        c.setRendered(true);

        c.result = obj;

        c.render(obj);
    }

    /**
     * for XHandler
     */
    @Override
    public void handle(XContext c) throws Throwable {
        XHandler m = findDo(c);
        Object obj = null;

        //m 不可能为 null；有 _def 打底
        if (m != null) {
            Boolean is_action = m instanceof XAction;
            //预加载控制器，确保所有的处理者可以都可以获取控制器
            if (is_action) {
                if(allowReadyController()) {
                    //提前准备控制器?（通过拦截器产生的参数，需要懒加载）
                    obj = ((XAction) m).bean().get();
                    c.attrSet("controller", obj);
                }

                c.attrSet("action", m);
            }

            handle0(c, m, obj, is_action);
        }
    }

    private void handle0(XContext c, XHandler m, Object obj, Boolean is_action) throws Throwable {
        /**
         * 1.保持与XAction相同的逻辑
         * */

        //前置处理（最多一次渲染）
        handleDo(c, () -> {
            for (XHandler h : befores) {
                h.handle(c);
            }
        });

        //主处理（最多一次尝染）
        if (c.getHandled() == false) {
            handleDo(c, () -> {
                if (is_action) {
                    ((XAction) m).invoke(c, obj);
                } else {
                    m.handle(c);
                }
            });
        } else {
            render(c.result, c);
        }

        //后置处理（确保不受前面的异常影响）
        for (XHandler h : afters) {
            h.handle(c);
        }
    }

    protected void handleDo(XContext c, RunnableEx runnable) throws Throwable {
        try {
            runnable.run();
        } catch (DataThrowable ex) {
            c.setHandled(true); //停止处理

            render(ex, c);
        } catch (Throwable ex) {
            c.setHandled(true); //停止处理

            c.attrSet("error", ex);
            render(ex, c);
            XEventBus.push(ex);
        }
    }


    /**
     * 添加前置拦截器
     */
    @XNote("添加前置拦截器")
    public <T extends XHandler> void before(Class<T> interceptorClz) {
        super.before(Aop.get(interceptorClz));
    }


    /**
     * 添加后置拦截器
     */
    @XNote("添加后置拦截器")
    public <T extends XHandler> void after(Class<T> interceptorClz) {
        super.after(Aop.get(interceptorClz));
    }

    /**
     * 添加接口
     */
    @XNote("添加接口")
    public void add(Class<?> beanClz) {
        if (beanClz != null) {
            BeanWrap bw = Aop.wrapAndPut(beanClz);

            add(bw, bw.remoting());
        }
    }

    /**
     * 添加接口（remoting ? 采用@json进行渲染）
     */
    @XNote("添加接口")
    public void add(Class<?> beanClz, boolean remoting) {
        if (beanClz != null) {
            add(Aop.wrapAndPut(beanClz), remoting);
        }
    }

    @XNote("添加接口")
    public void add(BeanWrap beanWp) {
        add(beanWp, beanWp.remoting());
    }

    /**
     * 添加接口（适用于，从Aop工厂遍历加入；或者把rpc代理包装成bw）
     */
    @XNote("添加接口")
    public void add(BeanWrap beanWp, boolean remoting) {
        if (beanWp == null) {
            return;
        }

        XHandlerLoader uw = new XHandlerLoader(beanWp, _path, remoting, this, allowActionMapping());

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
    @XNote("添加二级路径处理")
    public void add(String path, XHandler handler) {
        addDo(path, handler);
    }

    protected void addDo(String path, XHandler handler) {
        //addPath 已处理 path1= null 的情况
        if(allowPathMerging()) {
            _main.put(XUtil.mergePath(_path, path).toUpperCase(), handler);
        }else{
            _main.put(XUtil.mergePath(null, path).toUpperCase(), handler);
        }
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
