package org.noear.solon;

import org.noear.solon.core.Aop;
import org.noear.solon.core.*;
import org.noear.solon.ext.Act1;
import org.noear.solon.ext.Act2;
import org.noear.solon.ext.PrintUtil;

import java.util.*;

/**
 * 插件式微型Web框架(70kb)
 *
 * 框架主要有几部份组成：
 * 1.微框架体系
 * 2.插件体系
 * 3.注解体系(为 mvc, prc 提供支持) //Bean分为：普通Bean和Web Bean
 *
 * ///设计目标::
 * 1.更高的性能（弱化字段级注入，减少不必要的反射）
 * 2.更轻量的结构、更强的扩展性
 * 3.为Spring之外提供另一个选择
 *
 * ///保持手写和注解两种体验方案::
 *
 * ///关于Bean扫描和加载机制::
 * #XBean 为一般bean,会被加载 (仅支持类级别)
 * #XController, #XInterceptor 为特定bean,会被加载 (仅支持类级别)
 *
 * 其中：#XController (控制器), #XInterceptor (拦截器), #XBean(remoting=true)(服务)  会自动注入到 XApp.router
 *      //这三者最终都会转换为：XAction
 *
 * 其中：#XController 加在 XHandler上， 会自动注入到 XApp.router
 *      #XBean 加在 XPlugin上，会自运注入到 XApp.plug()
 *      #XBean 加在普通类上，会自动注入到 XApp.beans
 *
 * ///插件(XPlugin)的作用::
 * 1.扩展框架机能
 * 2.按需定制架构
 * 3.可让业务开发时分散、打包时合并；
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

    /**
     * 唯一实例
     */
    public static XApp global() {
        return _global;
    }


    /**
     * 启动应用（全局只启动一个），执行序列
     * <p>
     * 1.加载配置（约定：application.properties    为应用配置文件）
     * 2.加载自发现插件（约定：/solonplugin/*.properties 为插件配置文件）
     * 3.加载注解Bean（约定：@XBean,@XController,@XInterceptor 为bean）
     * 4.执行Bean加载事件（采用：注册事件的方式进行安需通知）
     */
    public static XApp start(Class<?> source, String[] args) {
        return start(source, args, null);
    }

    public static XApp start(Class<?> source, String[] args, Act1<XApp> builder){
        //1.初始化应用，加载配置
        XMap argx = XMap.from(args);
        return start(source,argx,builder);
    }

    public static XApp start(Class<?> source, XMap argx, Act1<XApp> builder) {
        if (_global != null) {
            return _global;
        }

        long time_start = System.currentTimeMillis();
        PrintUtil.blueln("solon.plugin:: Start loading");

        //1.初始化应用
        _global = new XApp(argx);

        //2.尝试加载扩展文件夹
        String _extend = argx.get("extend");
        if(XUtil.isEmpty(_extend)){
            _extend = _global.prop().get("solon.extend");
        }

        ExtendLoader.load(_extend, argx);

        //3.1.尝试扫描插件
        _global.prop().plugsScan();

        //3.2.尝试预构建
        if (builder != null) {
            builder.run(_global);
        }

        //3.3.尝试加载插件（顺序不能乱）
        _global.prop().plugs().forEach(p->p.start());

        //4.再加载bean
        if (source != null) {
            _global.loadBean(source);
        }

        //6.加载渲染关系
        XMap map = _global.prop().getXmap("solon.view.mapping");
        map.forEach((k, v) -> {
            XRenderManager.mapping("." + k, v);
        });

        long time_end = System.currentTimeMillis();
        PrintUtil.blueln("solon.plugin:: End loading @" + (time_end - time_start) + "ms");

        return _global;
    }

    /**
     * 停止服务（为web方式停止服务提供支持）
     */
    public static void stop() {
        if (_global == null) {
            return;
        }

        _global.prop().plugs().forEach(p->p.stop());
    }

    //////////////////////////////////

    /**
     * 加载Bean
     * */
    public void loadBean(Class<?> source){
        Aop.beanLoad(source);
    }

    /**
     * 共享变量（一般用于插件之间）
     * */
    private final Set<Act2<String,Object>> _onSharedAdd_event=new HashSet<>();
    private final Map<String,Object> _shared=new HashMap<>();
    private Map<String,Object> _shared_unmod;

    public void sharedAdd(String key,Object obj) {
        _shared.put(key, obj);
        _onSharedAdd_event.forEach(fun->{
            fun.run(key,obj);
        });
    }

    public <T> void sharedGet(String key,Act1<T> event) {
        Object tmp = _shared.get(key);
        if (tmp != null) {
            event.run((T) tmp);
        } else {
            onSharedAdd((k, v) -> {
                if (k.equals(key)) {
                    event.run((T) v);
                }
            });
        }
    }

    public void onSharedAdd(Act2<String,Object> event){
        _onSharedAdd_event.add(event);
    }

    public Map<String,Object> shared(){
        if(_shared_unmod == null) {
            _shared_unmod = Collections.unmodifiableMap(_shared);
        }

        return _shared_unmod;
    }

    /**
     * 路由器
     */
    private final XRouter _router;

    public XRouter router() {
        return _router;
    }

    /**
     * 端口
     */
    private final int _port;
    /**
     * 属性配置
     */
    private final XProperties _prop;

    protected XApp(XMap args) {
        _prop = new XProperties().load(args);
        _port = _prop.serverPort();

        //顺序不能换
        _router = new XRouter();

        _handler = new XRouterHandler(_router);
    }

    /**
     * 获取端口
     */
    public int port() {
        return _port;
    }

    /**
     * 获取属性
     */
    public XProperties prop() {
        return _prop;
    }


    /**
     * 插入插件
     */
    public void plug(XPlugin plugin) {
        plugin.start(this);
    }

    ///////////////////////////////////////////////
    //
    // 以下为web handler 有关
    //
    //////////////////////////////////////////////

    /**
     * 前置监听
     */
    public void before(String expr, XMethod method, XHandler handler) {
        _router.add(expr, XEndpoint.before, method, handler);
    }
    public void before(String expr, XMethod method, int index, XHandler handler) {
        _router.add(expr, XEndpoint.before, method, index, handler);
    }

    /**
     * 重置监听
     */
    public void after(String expr, XMethod method, XHandler handler) {
        _router.add(expr, XEndpoint.after, method, handler);
    }
    public void after(String expr, XMethod method, int index, XHandler handler) {
        _router.add(expr, XEndpoint.after, method, index, handler);
    }

    /**
     * 主体监听
     */
    public void add(String expr, XMethod method, XHandler handler) {
        _router.add(expr, XEndpoint.main, method, handler);
    }

    /**
     * 添加所有方法的监听（GET,POST,PUT,PATCH,DELETE,HEAD）
     */
    public void all(String path, XHandler handler) {
        add(path, XMethod.HTTP, handler);
    }

    //http

    /**
     * 添加GET方法的监听（REST.select 从服务端获取一或多项资源）
     */
    public void get(String path, XHandler handler) {
        add(path, XMethod.GET, handler);
    }

    /**
     * 添加POST方法的监听（REST.create 在服务端新建一项资源）
     */
    public void post(String path, XHandler handler) {
        add(path, XMethod.POST, handler);
    }

    /**
     * 添加PUT方法的监听（REST.update 客户端提供改变后的完整资源）
     */
    public void put(String path, XHandler handler) {
        add(path, XMethod.PUT, handler);
    }

    /**
     * 添加PATCH方法的监听（REST.update 客户端提供改变的属性）
     */
    public void patch(String path, XHandler handler) {
        add(path, XMethod.PATCH, handler);
    }

    /**
     * 添加DELETE方法的监听（REST.delete 从服务端删除资源）
     */
    public void delete(String path, XHandler handler) {
        add(path, XMethod.DELETE, handler);
    }

    /**
     * 添加SEND方法的监听（on web send）
     */
    public void send(String path, XHandler handler){
        add(path, XMethod.SEND, handler);
    }

    /**
     * XApp Handler
     */
    private XHandler _handler = null;

    public XHandler handlerGet() {
        return _handler;
    }

    public void handlerSet(XHandler handler) {
        if (handler != null) {
            _handler = handler;
        }
    }

    /**
     * 统一代理入口
     */
    @Override
    public void handle(XContext context) throws Throwable {
        try {
            //设置当前线程上下文
            XContextUtil.currentSet(context);

            _handler.handle(context);

        } catch (Throwable ex) {
            if (_onErrorEvent != null) {
                _onErrorEvent.run(context, ex);
            }else {
                throw ex;
            }
        } finally {
            //移除当前线程上下文
            XContextUtil.currentRemove();
        }
    }

    private Act2<XContext,Throwable> _onErrorEvent;

    public XApp onError(Act2<XContext,Throwable> event) {
        _onErrorEvent = event;
        return this;
    }
}
