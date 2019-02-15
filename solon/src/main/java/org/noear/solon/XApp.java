package org.noear.solon;

import org.noear.solon.core.Aop;
import org.noear.solon.core.XRender;
import org.noear.solon.core.*;

import java.util.*;

/**
 * 插件式Web微框架(50kb-)
 *
 * 整个框架主要有几部份组成：
 * 1.通用微框架体系
 * 2.插件体系
 * 3.简易注解体系(可实现 mvc,prc) //Bean分为：普通Bean和Web Bean
 *
 * ///设计目标::
 * 1.更高的性能（不支持字段级注入）
 * 2.更轻量的结构
 * 3.为Spring之后提供另一个选择
 *
 * ///保持手写和注解两种体验方案::
 *
 * ///关于Bean扫描和加载机制::
 * @XBean 为一般bean,会被加载 (仅支持类级别)
 * @XController, @XInterceptor, @XService 为特定bean,会被加载 (仅支持类级别)
 *
 * 其中：@XController (控制器), @XInterceptor (拦截器), @XService(服务)  会自动注入到 XApp.router
 *      //这三者最终都会转换为：XAction
 *
 * 其中：@XBean 加在 XHandler上， 会自动注入到 XApp.router
 *      @XBean 加在 XPlugin上，会自运注入到 XApp.plug()
 *      @XBean 加在普通类上，会自动注入到 XApp.beans
 *
 * ///插件(XPlugin)的作用::
 * 1.扩展框架机能
 * 2.让业务开发时分散、打包时合并；
 *
 * ///XMapping的策略
 * 1.与Spring保持相近
 * 2.编写更好性能的方法（根级map: /开头，子级map: 不要/开头；）
 *
 * ///更新日志：
 * 20190109:为path var添加_支持
 * 20190110:添加stop事件支持；
 *          添加XContent.paramValues(k)->[]；
 *          添加XContent.paramAsEntity(c)->t;
 *          添加XParam，支持XAction模型参数
 * 20190111:添加Aop扩展机制
 * */
public class XApp implements XHandler {
    private static XApp _global;

    /**唯一实例*/
    public static XApp global(){
        return _global;
    }

    /**
     * 启动应用（全局只启动一个），执行序列
     *
     * 1.加载配置（约定：application.properties    为应用配置文件）
     * 2.加载自发现插件（约定：/solonplugin/*.properties 为插件配置文件）
     * 3.加载注解Bean（约定：@XBean,@XController,@XInterceptor 为bean）
     * 4.执行Bean加载事件（采用：注册事件的方式进行安需通知）
     *
     * */
    public static XApp start(Class<?> source, String[] args) {
        if (_global != null) {
            return _global;
        }

        long time_start = System.currentTimeMillis();
        System.out.println("solon.boot:: start begin");

        //1.初始化应用，加载配置
        _global = new XApp(args);

        //3.然后加载插件（顺序不能乱）
        for (String p : _global.prop().plugs()) {
            XPlugin p1 = XUtil.newClass(p);
            if (p1 != null) {
                _global.plug(p1);
            }
        }

        //4.再加载bean
        if (source != null) {
            Aop.beanLoad(source);
        }

        long time_end = System.currentTimeMillis();
        System.out.println("solon.boot:: start end @" + (time_end - time_start) + "ms");

        return _global;
    }

    /** 停止服务（为web方式停止服务提供支持） */
    public static void stop() {
        if (_global == null) {
            return;
        }

        _global._stopEvent.forEach(f->f.run());
    }

    //////////////////////////////////

    /** 停目事件 */
    private Set<Runnable> _stopEvent = new LinkedHashSet<>();
    /** 渲染器 */
    private XRender _render = (d,c)->{if(d!=null){c.output(d.toString());}};

    /** 路由器 */
    private XRouter<XHandler> _router;
    /** 端口 */
    private int _port;
    /** 属性配置 */
    private XProperties _prop;

    protected XApp(String[] args) {
        _router = new XRouter();
        _prop = new XProperties(args);

        _port = _prop.serverPort();
    }

    /** 注删停止事件 */
    public void onStop(Runnable event){
        _stopEvent.add(event);
    }

    /** 获取端口 */
    public int port() {
        return _port;
    }

    /** 获取属性 */
    public XProperties prop() {
        return _prop;
    }

    ///////////////////////////////////////////////
    /** 获取视图渲染器 */
    public XRender render(){
        return _render;
    }
    /** 渲染数据 */
    public void render(Object obj, XContext ctx) throws Exception{
        render().render(obj, ctx);
    }
    /** 设置视图渲染器 */
    public void renderSet(XRender render) {
        _render = render;
    }

    /** 插入插件 */
    public void plug(XPlugin plugin) {
        plugin.start(this);
    }

    ///////////////////////////////////////////////
    //
    // 以下为web handler 有关
    //
    //////////////////////////////////////////////

    /** 前置监听 */
    public void before( String expr, String method, XHandler handler) {
        _router.add(expr, XEndpoint.before, method, handler);
    }

    /** 重置监听 */
    public void after(String expr, String method, XHandler handler) {
        _router.add(expr, XEndpoint.after, method, handler);
    }

    /** 主体监听 */
    public void add(String expr, String method, XHandler handler) {
        _router.add(expr, XEndpoint.main, method, handler);
    }

    //添加所有方法的监听
    public void all(String path, XHandler handler) {
        add(path, XMethod.ALL, handler);
    }

    //http

    //添加GET方法的监听
    public void get(String path, XHandler handler) {
        add(path, XMethod.GET, handler);
    }

    //添加POST方法的监听
    public void post(String path, XHandler handler) {
        add(path, XMethod.POST, handler);
    }

    //添加PUT方法的监听
    public void put(String path, XHandler handler) {
        add(path, XMethod.PUT, handler);
    }

    //添加DELETE方法的监听
    public void delete(String path, XHandler handler) {
        add(path, XMethod.DELETE, handler);
    }


    /////////////////////////////
    /** 当前上下文处理 */
    private final static ThreadLocal<XContext> _threadLocal  = new ThreadLocal<>();
    public static XContext currentContext(){
        return _threadLocal.get();
    }

    /** 统一代理入口 */
    @Override
    public void handle(XContext context) throws Exception {
        try {
            //设置当前线程上下文

            _threadLocal.set(context);
            boolean _handled = false;

            //前置处理
            do_handle(context, XEndpoint.before);

            //主体处理
            if (context.getHandled() == false) {
                _handled = do_handle(context, XEndpoint.main);
            }

            //后置处理
            do_handle(context, XEndpoint.after); //前后不能反

            //汇总状态
            if (_handled) {
                if (context.status() < 1) {
                    context.status(200);
                }
                context.setHandled(true);
            }

            if (context.status() < 1) {
                context.status(404);
            }

        } finally {
            //移除当前线程上下文
            _threadLocal.remove();
        }
    }

    private boolean do_handle(XContext context, int endpoint) throws Exception {
        XHandler handler = _router.matched(context, endpoint);

        if (handler != null) {
            handler.handle(context);
            return true;
        } else {
            return false;
        }
    }
}
