package org.noear.solon;

import org.noear.solon.core.Aop;
import org.noear.solon.core.*;
import org.noear.solon.ext.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 应用管理中心
 * */
public class XApp implements XHandler,XHandlerSlots {
    private static XApp _global;

    /**
     * 唯一实例
     */
    public static XApp global() {
        return _global;
    }

    public static XAppProperties cfg(){
        return global().prop();
    }


    /**
     * 启动应用（全局只启动一个），执行序列
     *
     * <p>
     * 1.加载配置（约定：application.properties    为应用配置文件）
     * 2.加载自发现插件（约定：/solonplugin/*.properties 为插件配置文件）
     * 3.加载注解Bean（约定：@XBean,@XController,@XInterceptor 为bean）
     * 4.执行Bean加载事件（采用：注册事件的方式进行安需通知）
     */
    public static XApp start(Class<?> source, String[] args) {
        return start(source, args, null);
    }

    public static XApp start(Class<?> source, String[] args, ConsumerEx<XApp> builder){
        //1.初始化应用，加载配置
        XMap argx = XMap.from(args);
        return start(source,argx,builder);
    }

    public static XApp start(Class<?> source, XMap argx, ConsumerEx<XApp> builder) {
        if (_global != null) {
            return _global;
        }

        //添加关闭勾子
        Runtime.getRuntime().addShutdownHook(new Thread(()->stopDo(false)));

        //绑定类加载器
        XClassLoader.bindingThread();

        long time_start = System.currentTimeMillis();
        PrintUtil.blueln("solon.plugin:: Start loading");

        //1.初始化应用
        _global = new XApp(source, argx);

        //2.尝试加载扩展文件夹
        String _extend = argx.get("extend");
        if (XUtil.isEmpty(_extend)) {
            _extend = _global.prop().get("solon.extend");
        }

        ExtendLoader.load(_extend);

        //3.1.尝试扫描插件
        _global.prop().plugsScan();

        //3.2.尝试预构建
        if (builder != null) {
            try {
                builder.accept(_global);
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        }

        //3.3.尝试加载插件（顺序不能乱） //不能用forEach，以免当中有插进来
        List<XPluginEntity> plugs = _global.prop().plugs();
        for (int i = 0,len = plugs.size(); i < len; i++) {
            plugs.get(i).start();
        }

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
        stopDo(true);
    }

    private static void stopDo(boolean exit) {
        if (_global == null) {
            return;
        }

        _global.prop().plugs().forEach(p -> p.stop());
        _global = null;

        if (exit) {
            XUtil.commonPool.submit(()->{
                //支持延时退出
                int delay = XApp.cfg().getInt("solon.stop.delay",0);

                if(delay > 0){
                    Thread.sleep(delay);
                }
                System.exit(0);

                return null;
            });
        }
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
    private final Set<BiConsumer<String,Object>> _onSharedAdd_event=new HashSet<>();
    private final Map<String,Object> _shared=new HashMap<>();
    private Map<String,Object> _shared_unmod;

    /**
     * 添加共享对象
     * */
    public void sharedAdd(String key,Object obj) {
        _shared.put(key, obj);
        _onSharedAdd_event.forEach(fun->{
            fun.accept(key,obj);
        });
    }

    /**
     * 获取共享对象（异步获取）
     * */
    public <T> void sharedGet(String key, Consumer<T> event) {
        Object tmp = _shared.get(key);
        if (tmp != null) {
            event.accept((T) tmp);
        } else {
            onSharedAdd((k, v) -> {
                if (k.equals(key)) {
                    event.accept((T) v);
                }
            });
        }
    }

    /**
     * 共享对象添加事件
     * */
    public void onSharedAdd(BiConsumer<String,Object> event){
        _onSharedAdd_event.add(event);
    }

    /**
     * 共享对象
     * */
    public Map<String,Object> shared(){
        if(_shared_unmod == null) {
            _shared_unmod = Collections.unmodifiableMap(_shared);
        }

        return _shared_unmod;
    }


    private final XRouter _router;
    /**
     * 路由器
     */
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
    private final XAppProperties _prop;
    private final Class<?> _source;

    protected XApp(Class<?> source, XMap args) {
        _source = source;

        _prop = new XAppProperties().load(args);
        _port = _prop.serverPort();

        //顺序不能换
        _router = new XRouter();

        _handler = new XRouterHandler(_router);
    }

    public Class<?> source(){
        return _source;
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
    public XAppProperties prop() {
        return _prop;
    }


    /**
     * 插入插件
     */
    public void plug(XPlugin plugin) {
        XPluginEntity p = new XPluginEntity(plugin);
        p.start();
        prop().plugs().add(p);
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
    @Override
    public void before(String expr, XMethod method, int index, XHandler handler) {
        _router.add(expr, XEndpoint.before, method, index, handler);
    }

    /**
     * 重置监听
     */
    public void after(String expr, XMethod method, XHandler handler) {
        _router.add(expr, XEndpoint.after, method, handler);
    }
    @Override
    public void after(String expr, XMethod method, int index, XHandler handler) {
        _router.add(expr, XEndpoint.after, method, index, handler);
    }

    /**
     * 主体监听
     */
    @Override
    public void add(String expr, XMethod method, XHandler handler) {
        _router.add(expr, XEndpoint.main, method, handler);
    }

    public void add(String expr, Class<?> clz) {
        BeanWrap bw = Aop.wrap(clz);
        if (bw != null) {
            new BeanWebWrap(bw, expr).load(this);
        }
    }

    public void add(String expr, Class<?> clz, boolean remoting) {
        BeanWrap bw = Aop.wrap(clz);
        if (bw != null) {
            new BeanWebWrap(bw, expr, remoting).load(this);
        }
    }

    /**
     * 添加所有方法监听
     */
    public void all(String path, XHandler handler) {
        add(path, XMethod.ALL, handler);
    }

    //http

    /**
     * 添加HTTP所有方法的监听（GET,POST,PUT,PATCH,DELETE,HEAD）
     */
    public void http(String path, XHandler handler) {
        add(path, XMethod.HTTP, handler);
    }

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
     * 添加web socket方法的监听
     */
    public void ws(String path, XHandler handler){
        add(path, XMethod.WEBSOCKET, handler);
    }

    /**
     * 添加socket方法的监听
     */
    public void socket(String path, XHandler handler){
        add(path, XMethod.SOCKET, handler);
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
    public void handle(XContext x) throws Throwable {
        try {
            //设置当前线程上下文
            XContextUtil.currentSet(x);
            _handler.handle(x);
        } catch (Throwable ex) {
            XMonitor.sendError(x, ex);
            throw ex;
        } finally {
            //移除当前线程上下文
            XContextUtil.currentRemove();
        }
    }

    /*
    * 异常时，自动500处理
    * */
    public void tryHandle(XContext x) {
        try {
            handle(x);
        } catch (Throwable ex) {
            x.status(500);
            x.setHandled(true);
            x.output(XUtil.getFullStackTrace(ex));
        }
    }

    public XApp onError(XEventHandler<Throwable> handler) {
        XMonitor.onError(handler);
        return this;
    }

    public boolean enableHttp = true;
    public boolean enableWebSocket = true;
    public boolean enableSocket = true;
}
