package org.noear.solon;

import org.noear.solon.annotation.XImport;
import org.noear.solon.core.Aop;
import org.noear.solon.core.*;
import org.noear.solon.ext.*;
import org.noear.solon.core.XListener;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 应用管理中心
 * */
public class XApp implements XHandler,XHandlerSlots {
    private static XApp _global;

    /**
     * 全局实例
     */
    public static XApp global() {
        return _global;
    }

    /**
     * 应用配置
     * */
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
        Runtime.getRuntime().addShutdownHook(new Thread(()->stop(false, 0)));

        //绑定类加载器
        XClassLoader.bindingThread();

        long time_start = System.currentTimeMillis();
        PrintUtil.blueln("solon.plugin:: Start loading");

        //1.初始化应用
        _global = new XApp(source, argx);

        //2.尝试加载扩展文件夹
        ExtendLoader.load(_global.prop().extend());

        //3.1.尝试扫描插件
        _global.prop().plugsScan();

        //3.2.尝试预构建
        if (builder != null) {
            try {
                builder.accept(_global);
            } catch (Throwable ex) {
                XUtil.throwableWrap(ex);
            }
        }

        //3.3.尝试加载插件（顺序不能乱） //不能用forEach，以免当中有插进来
        List<XPluginEntity> plugs = _global.prop().plugs();
        for (int i = 0,len = plugs.size(); i < len; i++) {
            plugs.get(i).start();
        }

        //3.4.通过注解导入bean（一般是些配置器）
        _global.importTry();


        //4.再扫描bean
        if (source != null) {
            Aop.context().beanScan(source);
        }


        //5.加载渲染关系
        XMap map = _global.prop().getXmap("solon.view.mapping");
        map.forEach((k, v) -> {
            XBridge.renderMapping("." + k, v);
        });

        //6.bean加载完成
        Aop.context().beanLoaded();

        long time_end = System.currentTimeMillis();
        PrintUtil.blueln("solon.plugin:: End loading @" + (time_end - time_start) + "ms");

        return _global;
    }

    //通过注解，导入bean
    private void importTry() {
        if (_source == null) {
            return;
        }

        for (Annotation a1 : _source.getAnnotations()) {
            if (a1 instanceof XImport) {
                import0((XImport) a1);
            } else {
                import0(a1.annotationType().getAnnotation(XImport.class));
            }
        }
    }

    private void import0(XImport anno) {
        if (anno != null) {
            for (Class<?> clz : anno.value()) {
                beanMake(clz);
            }
        }
    }

    /**
     * 停止服务（为web方式停止服务提供支持）
     */
    private static boolean _stopped;
    public static void stop() {
        stop(true, 0);
    }

    public static void stop(boolean exit, long delay) {
        if (_global == null) {
            return;
        }

        _stopped = true;

        XUtil.commonPool.submit(() -> {
            if (delay > 0) {
                Thread.sleep(delay);
            }

            _global.prop().plugs().forEach(p -> p.stop());
            _global = null;

            if (exit) {
                System.exit(0);
            }
            return null;
        });
    }

    //////////////////////////////////

    /**
     * 共享变量（一般用于插件之间）
     * */
    private final Set<BiConsumer<String,Object>> _onSharedAdd_event=new HashSet<>();
    private final Map<String,Object> _shared=new HashMap<>();
    private Map<String,Object> _shared_unmod;

    /**
     * 根据源加载bean
     * */
    public void beanScan(Class<?> source){
        Aop.context().beanScan(source);
    }

    /**
     * 根据类型构制bean
     * */
    public BeanWrap beanMake(Class<?> clz){
        return Aop.context().beanMake(clz);
    }

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

    private final int _port; //端口
    private final XAppProperties _prop; //属性配置
    private final Class<?> _source; //应用加载源

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
    public void before(String expr,  XHandler handler) {
        before(expr, XMethod.ALL, handler);
    }

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
    public void after(String expr,XHandler handler) {
        after(expr, XMethod.ALL, handler);
    }

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
        BeanWrap bw = Aop.wrapAndPut(clz);
        if (bw != null) {
            new BeanWebWrap(bw, expr).load(this);
        }
    }

    public void add(String expr, Class<?> clz, boolean remoting) {
        BeanWrap bw = Aop.wrapAndPut(clz);
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
     * 添加web socket方法的监听
     */
    public void ws(String path, XListener listener){
        _router.add(path, XMethod.WEBSOCKET, listener);
    }

    /**
     * 添加socket方法的监听
     */
    public void socket(String path, XHandler handler){
        add(path, XMethod.SOCKET, handler);
    }

    /**
     * 添加socket方法的监听
     */
    public void socket(String path, XListener listener){
        _router.add(path, XMethod.SOCKET, listener);
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
        if(_stopped){
            //停止后不再接收请求（避免产生脏数据）
            return;
        }

        try {
            //设置当前线程上下文
            XContextUtil.currentSet(x);
            _handler.handle(x);
        } catch (Throwable ex) {
            XEventBus.push(ex);
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
            ex = XUtil.throwableUnwrap(ex);

            x.statusSet(500);
            x.setHandled(true);
            x.output(XUtil.getFullStackTrace(ex));
        }
    }

    public XApp onError(XEventListener<Throwable> handler) {
        XEventBus.subscribe(Throwable.class, handler);
        return this;
    }

    public <T> XApp onEvent(Class<T> type, XEventListener<T> handler) {
        XEventBus.subscribe(type, handler);
        return this;
    }

    /**
     * 启用Http信号接入
     * */
    private boolean _enableHttp = true;
    public boolean enableHttp(){
        return _enableHttp;
    }
    public XApp enableHttp(boolean enable){
        _enableHttp = enable;
        return this;
    }
    /**
     * 启用WebSocket信号接入
     * */
    private boolean _enableWebSocket = false;
    public boolean enableWebSocket(){
        return _enableWebSocket;
    }
    public XApp enableWebSocket(boolean enable){
        _enableWebSocket = enable;
        return this;
    }
    /**
     * 启用Socket信号接入
     * */
    private boolean _enableSocket = false;
    public boolean enableSocket(){
        return _enableSocket;
    }
    public XApp enableSocket(boolean enable){
        _enableSocket = enable;
        return this;
    }
    /**
     * 启用事务
     * */
    private boolean _enableTransaction = true;
    public boolean enableTransaction(){
        return _enableTransaction;
    }
    public XApp enableTransaction(boolean enable){
        _enableTransaction = enable;
        return this;
    }
    /**
     * 启用缓存
     * */
    private boolean _enableCaching = true;
    public boolean enableCaching(){
        return _enableCaching;
    }
    public XApp enableCaching(boolean enable){
        _enableCaching = enable;
        return this;
    }
    /**
     * 启用静态文件
     * */
    private boolean _enableStaticfiles = true;
    public boolean enableStaticfiles(){
        return _enableStaticfiles;
    }
    public XApp enableStaticfiles(boolean enable){
        _enableStaticfiles = enable;
        return this;
    }
}
