package org.noear.solon.core.handle;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.*;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.PathUtil;
import org.noear.solon.ext.DataThrowable;

import java.util.*;
import java.util.function.Predicate;


/**
 * 本地网关
 * 提供容器，重新组织处理者运行；只支持HASH路由
 *
 * <pre><code>
 * @Mapping("/*")
 * @Controller
 * public class ApiGateway extends Gateway {
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
public abstract class Gateway extends HandlerAide implements Handler, Render {
    private Handler mainDef;
    private final Map<String, Handler> main = new HashMap<>();
    private final String mapping;
    private Mapping mappingAnno;
    private List<FilterEntity> filterList = new ArrayList<>();

    public Gateway() {
        super();
        mappingAnno = this.getClass().getAnnotation(Mapping.class);
        if (mappingAnno == null) {
            throw new RuntimeException("No Mapping!");
        }

        mapping = Utils.annoAlias(mappingAnno.value(), mappingAnno.path());

        //默认为404错误输出
        mainDef = (c) -> c.status(404);

        filterList.add(new FilterEntity(Integer.MAX_VALUE, this::doFilter));

        register();
    }

    /**
     * 注册相关接口与拦截器
     */
    @Note("注册相关接口与拦截器")
    protected abstract void register();


    /**
     * 允许 Action Mapping 申明
     */
    @Note("允许 Action Mapping 申明")
    protected boolean allowActionMapping() {
        return true;
    }

    /**
     * 充许提前准备控制器
     */
    @Note("充许提前准备控制器")
    protected boolean allowReadyController() {
        return true;
    }

    /**
     * 充许路径合并
     */
    @Note("充许路径合并")
    protected boolean allowPathMerging() {
        return true;
    }


    /**
     * for Render （用于接管 BeanWebWrap 和 Action 的渲染）
     */
    @Override
    public void render(Object obj, Context c) throws Throwable {
        if (c.getRendered()) {
            return;
        }

        if (obj instanceof DataThrowable) {
            return;
        }

        c.result = obj;
        c.render(obj);
    }

    /**
     * 添加过滤器（按先进后出策略执行）
     *
     * @param filter 过滤器
     */
    public void filter(Filter filter) {
        filter(0, filter);
    }

    public void filter(int index, Filter filter) {
        filterList.add(new FilterEntity(index, filter));
        filterList.sort(Comparator.comparingInt(f -> f.index));
    }

    /**
     * for Handler
     */
    @Override
    public void handle(Context c) throws Throwable {
        try {
            new FilterChainNode(filterList).doFilter(c);
        } catch (Throwable e) {
            c.setHandled(true); //停止处理

            e = Utils.throwableUnwrap(e);

            if (e instanceof DataThrowable) {
                DataThrowable ex = (DataThrowable) e;

                if (ex.data() == null) {
                    render(ex, c);
                } else {
                    render(ex.data(), c);
                }
            } else {
                c.errors = e;

                //1.推送事件（先于渲染，可做自定义渲染）
                EventBus.push(e);

                //2.渲染
                if (c.result == null) {
                    render(e, c);
                } else {
                    render(c.result, c);
                }
            }
        }
    }

    protected void doFilter(Context c, FilterChain chain) throws Throwable {
        Handler m = find(c);
        Object obj = null;

        //m 不可能为 null；有 _def 打底
        if (m != null) {
            Boolean is_action = m instanceof Action;
            //预加载控制器，确保所有的处理者可以都可以获取控制器
            if (is_action) {
                if (allowReadyController()) {
                    //提前准备控制器?（通过拦截器产生的参数，需要懒加载）
                    obj = ((Action) m).controller().get();
                    c.attrSet("controller", obj);
                }

                c.attrSet("action", m);
            }

            handle0(c, m, obj, is_action);
        }
    }

    private void handle0(Context c, Handler m, Object obj, Boolean is_action) throws Throwable {
        /**
         * 1.保持与XAction相同的逻辑
         * */

        //前置处理（最多一次渲染）
        try {
            for (Handler h : befores) {
                h.handle(c);
            }

            //主处理（最多一次尝染）
            if (c.getHandled() == false) {
                if (is_action) {
                    ((Action) m).invoke(c, obj);
                } else {
                    m.handle(c);
                }
            } else {
                render(c.result, c);
            }
        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);
            if (e instanceof DataThrowable) {
                DataThrowable ex = (DataThrowable)e;
                if (ex.data() == null) {
                    render(ex, c);
                } else {
                    render(ex.data(), c);
                }
            } else {
                c.errors = e; //为 afters，留个参考
                throw e;
            }
        } finally {

            //后置处理（确保不受前面的异常影响）
            for (Handler h : afters) {
                h.handle(c);
            }
        }
    }


    /**
     * 添加前置拦截器
     */
    @Note("添加前置拦截器")
    public <T extends Handler> void before(Class<T> interceptorClz) {
        super.before(Aop.getOrNew(interceptorClz));
    }


    /**
     * 添加后置拦截器
     */
    @Note("添加后置拦截器")
    public <T extends Handler> void after(Class<T> interceptorClz) {
        super.after(Aop.getOrNew(interceptorClz));
    }

    @Note("添加接口")
    public void addBeans(Predicate<BeanWrap> where) {
        addBeans(where, false);
    }

    /**
     * remoting 的 bean 建议一个个添加，并同时添加前缀 path
     */
    @Note("添加接口")
    public void addBeans(Predicate<BeanWrap> where, boolean remoting) {
        Aop.beanOnloaded(() -> {
            Aop.beanForeach(bw -> {
                if (where.test(bw)) {
                    if (remoting) {
                        add(bw, remoting); //强制为 remoting
                    } else {
                        add(bw); //自动判定
                    }
                }
            });
        });
    }

    /**
     * 添加接口
     */
    @Note("添加接口")
    public void add(Class<?> beanClz) {
        if (beanClz != null) {
            BeanWrap bw = Aop.wrapAndPut(beanClz);

            add(bw, bw.remoting());
        }
    }

    /**
     * 添加接口
     */
    @Note("添加接口")
    public void add(String path, Class<?> beanClz) {
        if (beanClz != null) {
            BeanWrap bw = Aop.wrapAndPut(beanClz);

            add(path, bw, bw.remoting());
        }
    }

    /**
     * 添加接口（remoting ? 采用@json进行渲染）
     */
    @Note("添加接口")
    public void add(Class<?> beanClz, boolean remoting) {
        if (beanClz != null) {
            add(Aop.wrapAndPut(beanClz), remoting);
        }
    }

    /**
     * 添加接口（remoting ? 采用@json进行渲染）
     */
    @Note("添加接口")
    public void add(String path, Class<?> beanClz, boolean remoting) {
        if (beanClz != null) {
            add(path, Aop.wrapAndPut(beanClz), remoting);
        }
    }

    @Note("添加接口")
    public void add(BeanWrap beanWp) {
        add(beanWp, beanWp.remoting());
    }

    @Note("添加接口")
    public void add(String path, BeanWrap beanWp) {
        add(path, beanWp, beanWp.remoting());
    }

    /**
     * 添加接口（适用于，从Aop工厂遍历加入；或者把rpc代理包装成bw）
     */
    @Note("添加接口")
    public void add(BeanWrap beanWp, boolean remoting) {
        add(null, beanWp, remoting);
    }

    @Note("添加接口")
    public void add(String path, BeanWrap beanWp, boolean remoting) {
        if (beanWp == null) {
            return;
        }

        HandlerLoader uw = new HandlerLoader(beanWp, mapping, remoting, this, allowActionMapping());

        uw.load((p1, m, h) -> {
            if (h instanceof Action) {
                Action h2 = (Action) h;

                if (path == null) {
                    add(h2.name(), h2);
                } else {
                    add(PathUtil.mergePath(path, h2.name()), h2);
                }
            }
        });
    }


    @Note("添加缺少处理")
    public void add(Handler handler) {
        addDo("", handler);
    }

    /**
     * 添加二级路径处理
     */
    @Note("添加二级路径处理")
    public void add(String path, Handler handler) {
        addDo(path, handler);
    }


    /**
     * 添加接口
     */
    protected void addDo(String path, Handler handler) {
        if (Utils.isEmpty(path)) {
            mainDef = handler;
            return;
        }

        //addPath 已处理 path1= null 的情况
        if (allowPathMerging()) {
            main.put(PathUtil.mergePath(mapping, path).toUpperCase(), handler);
        } else {
            main.put(path.toUpperCase(), handler);
        }
    }

    /**
     * 获取接口
     */
    protected Handler getDo(String path) {
        if (path == null) {
            return null;
        } else {
            return main.get(path);
        }
    }

    /**
     * 查找接口
     */
    protected Handler find(Context c) throws Throwable {
        return findDo(c, c.pathNew().toUpperCase());
    }

    protected Handler findDo(Context c, String path) throws Throwable {
        Handler h = getDo(path);

        if (h == null) {
            mainDef.handle(c);
            c.setHandled(true);
            return mainDef;
        } else {
            if (h instanceof Action) {
                c.attrSet("handler_name", ((Action) h).name());
            }
            return h;
        }
    }
}
