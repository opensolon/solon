package org.noear.solon.core.handle;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.*;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.route.RoutingDefault;
import org.noear.solon.core.route.RoutingTable;
import org.noear.solon.core.route.RoutingTableDefault;
import org.noear.solon.core.util.PathUtil;
import org.noear.solon.core.util.DataThrowable;
import org.noear.solon.core.util.RankEntity;

import java.util.*;
import java.util.function.Predicate;


/**
 * 本地网关。提供容器，重新组织'处理器'运行；
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
    //主处理缺省
    private Handler mainDef;
    //主处理路由
    private final RoutingTable<Handler> mainRouting;
    //映射
    private final String mapping;
    //映射注解
    private Mapping mappingAnno;
    //过滤列表
    private List<RankEntity<Filter>> filterList = new ArrayList<>();

    /**
     * 获取内部主路由（方便文档生成）
     * */
    public RoutingTable<Handler> getMainRouting() {
        return mainRouting;
    }

    public Gateway() {
        this(new RoutingTableDefault<>());
    }

    public Gateway(RoutingTable<Handler> routingTable) {
        super();
        mainRouting = routingTable;

        mappingAnno = this.getClass().getAnnotation(Mapping.class);
        if (mappingAnno == null) {
            throw new IllegalStateException("No Mapping!");
        }

        mapping = Utils.annoAlias(mappingAnno.value(), mappingAnno.path());

        //默认为404错误输出
        mainDef = (c) -> c.status(404);

        filterList.add(new RankEntity<>(this::doFilter, Integer.MAX_VALUE));

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
     * 允许提前准备控制器
     */
    protected boolean allowReadyController() {
        return true;
    }

    /**
     * 允许路径合并
     */
    protected boolean allowPathMerging() {
        return true;
    }

    /**
     * for Render （用于接管 BeanWebWrap 和 Action 的渲染）
     */
    @Override
    public void render(Object obj, Context c) throws Throwable {
        if (obj instanceof DataThrowable) {
            return;
        }

        if (c.getRendered() == false) {
            c.result = obj;
        }

        if (obj instanceof Throwable) {
            if (c.remoting()) {
                //尝试推送异常，不然没机会记录；也可对后继做控制
                EventBus.pushTry(obj);

                if (c.getRendered() == false) {
                    c.render(obj);
                }
            } else {
                c.setHandled(false); //传递给 filter, 可以统一处理未知异常
                throw (Throwable) obj;
            }
        } else {
            if (c.getRendered() == false) {
                c.render(obj);
            }
        }
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
        filterList.add(new RankEntity<>(filter, index));
        filterList.sort(Comparator.comparingInt(f -> f.index));
    }

    /**
     * for Handler
     */
    @Override
    public void handle(Context c) throws Throwable {
        try {
            new FilterChainImpl(filterList).doFilter(c);
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
                render(e, c);
            }
        }
    }

    protected void doFilter(Context c, FilterChain chain) throws Throwable {
        Handler m = find(c);
        Object obj = null;

        //m 不可能为 null；有 _def 打底
        if (m != null) {
            Boolean is_action = m instanceof Action;
            //预加载控制器，确保所有的'处理器'可以都可以获取控制器
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
                DataThrowable ex = (DataThrowable) e;
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
     * 添加前置处理器
     */
    public <T extends Handler> void before(Class<T> interceptorClz) {
        super.before(Solon.context().getBeanOrNew(interceptorClz));
    }


    /**
     * 添加后置处理器
     */
    public <T extends Handler> void after(Class<T> interceptorClz) {
        super.after(Solon.context().getBeanOrNew(interceptorClz));
    }

    /**
     * 添加接口
     * */
    public void addBeans(Predicate<BeanWrap> where) {
        addBeans(where, false);
    }

    /**
     * 添加接口（remoting 的 bean 建议一个个添加，并同时添加前缀 path）
     */
    public void addBeans(Predicate<BeanWrap> where, boolean remoting) {
        Solon.context().lifecycle(-98, () -> {
            Solon.context().beanForeach(bw -> {
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
    public void add(Class<?> beanClz) {
        if (beanClz != null) {
            BeanWrap bw = Solon.context().wrapAndPut(beanClz);

            add(bw, bw.remoting());
        }
    }

    /**
     * 添加接口
     */
    public void add(String path, Class<?> beanClz) {
        if (beanClz != null) {
            BeanWrap bw = Solon.context().wrapAndPut(beanClz);

            add(path, bw, bw.remoting());
        }
    }

    /**
     * 添加接口（remoting ? 采用@json进行渲染）
     */
    public void add(Class<?> beanClz, boolean remoting) {
        if (beanClz != null) {
            add(Solon.context().wrapAndPut(beanClz), remoting);
        }
    }

    /**
     * 添加接口（remoting ? 采用@json进行渲染）
     */
    public void add(String path, Class<?> beanClz, boolean remoting) {
        if (beanClz != null) {
            add(path, Solon.context().wrapAndPut(beanClz), remoting);
        }
    }

    /**
     * 添加接口
     * */
    public void add(BeanWrap beanWp) {
        add(beanWp, beanWp.remoting());
    }

    /**
     * 添加接口
     * */
    public void add(String path, BeanWrap beanWp) {
        add(path, beanWp, beanWp.remoting());
    }

    /**
     * 添加接口（适用于，从Aop工厂遍历加入；或者把rpc代理包装成bw）
     */
    public void add(BeanWrap beanWp, boolean remoting) {
        add(null, beanWp, remoting);
    }

    /**
     * 添加接口
     * */
    public void add(String path, BeanWrap beanWp, boolean remoting) {
        if (beanWp == null) {
            return;
        }

        Mapping bMapping = beanWp.clz().getAnnotation(Mapping.class);
        String bPath = null;
        if (bMapping != null) {
            bPath = Utils.annoAlias(bMapping.value(), bMapping.path());
        }

        HandlerLoader uw = new HandlerLoader(beanWp, bPath, remoting, this, allowActionMapping());

        uw.load((expr, method, handler) -> {
            if (path == null) {
                addDo(expr, method, handler);
            } else {
                addDo(PathUtil.mergePath(path, expr), method, handler);
            }
        });
    }

    /**
     * 添加默认接口处理
     * */
    public void add(Handler handler) {
        addDo("", MethodType.ALL, handler);
    }

    /**
     * 添加二级路径处理
     */
    public void add(String path, Handler handler) {
        addDo(path, MethodType.ALL, handler);
    }


    /**
     * 添加二级路径处理
     */
    public void add(String path, MethodType method, Handler handler) {
        addDo(path, method, handler);
    }

    /**
     * 添加接口
     */
    protected void addDo(String path, MethodType method, Handler handler) {
        if (Utils.isEmpty(path) || "/".equals(path)) {
            mainDef = handler;
            return;
        }

        //addPath 已处理 path1= null 的情况
        if (allowPathMerging()) {
            String path2 = PathUtil.mergePath(mapping, path);
            mainRouting.add(new RoutingDefault<>(path2, method, handler));
        } else {
            mainRouting.add(new RoutingDefault<>(path, method, handler));
        }
    }

    /**
     * 查找接口
     */
    protected Handler find(Context c) throws Throwable {
        if (mainRouting.count() == 0) {
            //如果没有记录，说明只有一个默认； 则默认是唯一主处理
            return findDo(c, null);
        } else {
            return findDo(c, c.pathNew());
        }
    }

    protected Handler findDo(Context c, String path) throws Throwable {
        Handler h;

        if (path == null) { //null 表示直接使用默认处理器，且不跳过
            h = mainDef;
        } else {
            h = getDo(c, path);
        }

        if (h == null) {
            mainDef.handle(c);
            c.setHandled(true);
            return mainDef;
        } else {
            if (h instanceof Action) {
                c.attrSet("handler_name", ((Action) h).fullName());
            }
            return h;
        }
    }



    /**
     * 获取接口
     */
    protected Handler getDo(Context c, String path) {
        if (path == null) {
            return null;
        } else {
            MethodType method = MethodTypeUtil.valueOf(c.method());
            return mainRouting.matchOne(path, method);
        }
    }
}
