/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.core.handle;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.*;
import org.noear.solon.core.route.RoutingDefault;
import org.noear.solon.core.route.RoutingTable;
import org.noear.solon.core.route.RoutingTableDefault;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.PathUtil;
import org.noear.solon.core.util.DataThrowable;

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
 *         filter(StartFilter.class);   //添加前置拦截器，开始计时+记录请求日志
 *         filter(IpFilter.class);      //添加前置拦截器，检查IP白名单
 *
 *         filter(EndFilter.class);      //添加后置拦截器，结束计时+记录输出日志+记录接口性能
 *
 *         add(DemoService.class, true); //添加接口
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * @since 3.0
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

    /**
     * 获取内部主路由（方便文档生成）
     */
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

        Solon.context().lifecycle(() -> {
            //通过生命周期触发注册，可以在注册时使用注入字段
            register();
        });
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
                Throwable objE = (Throwable) obj;
                LogUtil.global().warn("Gateway remoting handle failed!", objE);

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
     * for Handler
     */
    @Override
    public void handle(Context c) throws Throwable {
        try {
            handleDo(c);
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

    /**
     * 执行处理
     */
    protected void handleDo(Context c) throws Throwable {
        //预备
        prepareDo(c);

        //执行过滤与主处理
        new FilterChainImpl(filters(), this::mainDo).doFilter(c);
    }

    /**
     * 执行预备
     */
    protected void prepareDo(Context c) {
        //缓存处理
        String pathNewCached = c.attr("tmp_path_new_cached");
        if (pathNewCached != null && pathNewCached.equals(c.pathNew())) {
            return;
        } else {
            c.attrSet("tmp_path_new_cached", c.pathNew());
        }

        //查找处理器，并预处理
        Handler m = find(c);
        Object obj = null;

        //m 不可能为 null；有 _def 打底
        if (m != null) {
            c.attrSet(Constants.ATTR_MAIN_HANDLER, m);

            //预加载控制器，确保所有的'处理器'可以都可以获取控制器
            if (m instanceof Action) {
                if (allowReadyController()) {
                    //提前准备控制器?（通过拦截器产生的参数，需要懒加载）
                    obj = ((Action) m).controller().get(true);
                    c.attrSet(Constants.ATTR_CONTROLLER, obj);
                }
            }
        }
    }

    /**
     * 执行主处理
     */
    protected void mainDo(Context c) throws Throwable {
        //预处理
        prepareDo(c);

        //获取主处理
        Handler m = c.attr(Constants.ATTR_MAIN_HANDLER);

        //一般 m 不可能为 null；有 _def 打底
        if (m != null) {
            if (m == this) {
                //避免死循环
                return;
            }

            Object obj = c.attr(Constants.ATTR_CONTROLLER);
            boolean is_action = m instanceof Action;

            mainExec(c, m, obj, is_action);
        }
    }

    protected void mainBefores(Context c) throws Throwable {
        //预留（为兼容提供余地）
    }

    protected void mainAfters(Context c) throws Throwable {
        //预留（为兼容提供余地）
    }

    private void mainExec(Context c, Handler m, Object obj, boolean is_action) throws Throwable {
        /**
         * 1.保持与Action相同的逻辑
         * */

        try {
            //之前
            mainBefores(c);

            //主处理（最多一次渲染）
            if (c.getHandled() == false) { //保留这个，过滤器可以有两种控制方式（软控，硬控）
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
            //之后
            mainAfters(c);
        }
    }

    /**
     * 添加接口
     */
    public void addBeans(Predicate<BeanWrap> where) {
        addBeans(where, false);
    }

    /**
     * 添加接口（remoting 的 bean 建议一个个添加，并同时添加前缀 path）
     */
    public void addBeans(Predicate<BeanWrap> where, boolean remoting) {
        Solon.context().lifecycle(Constants.LF_IDX_GATEWAY_BEAN_USES, () -> {
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
     */
    public void add(BeanWrap beanWp) {
        add(beanWp, beanWp.remoting());
    }

    /**
     * 添加接口
     */
    public void add(String path, BeanWrap beanWp) {
        add(path, beanWp, beanWp.remoting());
    }

    /**
     * 添加接口（适用于，从应用容器遍历加入；或者把rpc代理包装成bw）
     */
    public void add(BeanWrap beanWp, boolean remoting) {
        add(null, beanWp, remoting);
    }

    /**
     * 添加接口
     */
    public void add(String path, BeanWrap beanWp, boolean remoting) {
        if (beanWp == null) {
            return;
        }

        Mapping bMapping = beanWp.clz().getAnnotation(Mapping.class);
        String bPath = null;
        if (bMapping != null) {
            bPath = Utils.annoAlias(bMapping.value(), bMapping.path());
        }

        ActionLoader uw = Solon.app().factoryManager().mvcFactory()
                .createLoader(beanWp, bPath, remoting, this, allowActionMapping());

        uw.load((expr, method, index, handler) -> {
            if (path == null) {
                addDo(expr, method, index, handler);
            } else {
                addDo(PathUtil.mergePath(path, expr), method, index, handler);
            }
        });
    }

    /**
     * 添加默认接口处理
     */
    public void add(Handler handler) {
        addDo("", MethodType.ALL, 0, handler);
    }

    /**
     * 添加二级路径处理
     */
    public void add(String path, Handler handler) {
        addDo(path, MethodType.ALL, 0, handler);
    }


    /**
     * 添加二级路径处理
     */
    public void add(String path, MethodType method, Handler handler) {
        addDo(path, method, 0, handler);
    }

    /**
     * 添加接口
     */
    protected void addDo(String path, MethodType method, int index, Handler handler) {
        if (Utils.isEmpty(path) || "/".equals(path)) {
            mainDef = handler;
            return;
        }

        //addPath 已处理 path1= null 的情况
        if (allowPathMerging()) {
            String path2 = PathUtil.mergePath(mapping, path);
            mainRouting.add(new RoutingDefault<>(path2, method, index, handler));
        } else {
            mainRouting.add(new RoutingDefault<>(path, method, index, handler));
        }
    }

    /**
     * 查找接口
     */
    public Handler find(Context c) {
        if (mainRouting.count() == 0) {
            //如果没有记录，说明只有一个默认； 则默认是唯一主处理
            return findDo(c, null);
        } else {
            return findDo(c, c.pathNew());
        }
    }

    protected Handler findDo(Context c, String path) {
        Handler h;

        if (path == null) { //null 表示直接使用默认处理器，且不跳过
            h = mainDef;
        } else {
            h = getDo(c, path);
        }

        if (h == null) {
            //mainDef.handle(c); //不能执行（会破坏规则统一）
            //c.setHandled(true);
            return mainDef;
        } else {
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