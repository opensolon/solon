package org.noear.solon;

import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.core.handler.*;
import org.noear.solon.core.route.Router;
import org.noear.solon.core.route.RouterHandler;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.annotation.Import;
import org.noear.solon.core.Aop;
import org.noear.solon.core.*;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.BeanLoadEndEvent;
import org.noear.solon.core.event.PluginLoadEndEvent;
import org.noear.solon.ext.*;
import org.noear.solon.core.message.MessageListener;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 应用管理中心
 *
 * <pre><code>
 * public class DemoApp{
 *     public static void main(String[] args){
 *         Solon.start(DemoApp.class, args);
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
public class Solon implements Handler, HandlerSlots {
    private static Solon global;

    /**
     * 全局实例
     */
    public static Solon global() {
        return global;
    }

    /**
     * 应用配置
     * */
    public static SolonProps cfg(){
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
    public static Solon start(Class<?> source, String[] args) {
        return start(source, args, null);
    }

    public static Solon start(Class<?> source, String[] args, ConsumerEx<Solon> initialize) {
        //1.初始化应用，加载配置
        NvMap argx = NvMap.from(args);
        return start(source, argx, initialize);
    }

    public static Solon start(Class<?> source, NvMap argx, ConsumerEx<Solon> initialize) {
        if (global != null) {
            return global;
        }

        //绑定类加载器
        ClassLoaderX.bindingThread();

        //添加关闭勾子
        Runtime.getRuntime().addShutdownHook(new Thread(()->stop(false, 0)));

        long time_start = System.currentTimeMillis();
        PrintUtil.blueln("solon.App:: Start loading");

        //1.创建应用
        global = new Solon(source, argx);
        global.init();

        //2.尝试初始化
        if (initialize != null) {
            try {
                initialize.accept(global);
            } catch (Throwable ex) {
                throw Utils.throwableWrap(ex);
            }
        }

        //3.运行
        global.run();

        long time_end = System.currentTimeMillis();
        PrintUtil.blueln("solon.App:: End loading @" + (time_end - time_start) + "ms");

        return global;
    }

    /**
     * 初始化（不能合在构建函数里）
     * */
    protected void init() {
        //a.尝试加载扩展文件夹
        String filterStr = prop().extendFilter();
        if (Utils.isEmpty(filterStr)) {
            //不需要过滤
            ExtendLoader.load(prop().extend(), false);
        } else {
            //增加过滤
            String[] filterS = filterStr.split(";");
            ExtendLoader.load(prop().extend(), false, (path) -> {
                for (String f : filterS) {
                    if (path.contains(f)) {
                        return true;
                    }
                }

                return false;
            });
        }


        //b.尝试扫描插件
        prop().plugsScan();
    }

    /**
     * 运行应用
     * */
    protected void run() {
        //1.1.尝试启动插件（顺序不能乱） //不能用forEach，以免当中有插进来
        List<PluginEntity> plugs = prop().plugs();
        for (int i = 0, len = plugs.size(); i < len; i++) {
            plugs.get(i).start();
        }

        //event::1.x.推送Plugin load end事件
        EventBus.push(PluginLoadEndEvent.instance);


        //2.1.通过注解导入bean（一般是些配置器）
        importTry();

        //2.2.通过源扫描bean
        if (source() != null) {
            Aop.context().beanScan(source());
        }

        //event::2.x.推送Bean load end事件
        EventBus.push(BeanLoadEndEvent.instance);


        //3.加载渲染关系
        NvMap map = prop().getXmap("solon.view.mapping");
        map.forEach((k, v) -> {
            Bridge.renderMapping("." + k, v);
        });

        //3.1.标识上下文加载完成
        Aop.context().beanLoaded();

        //event::4.x.推送App load end事件
        EventBus.push(AppLoadEndEvent.instance);
    }

    //通过注解，导入bean
    protected void importTry() {
        if (_source == null) {
            return;
        }

        for (Annotation a1 : _source.getAnnotations()) {
            if (a1 instanceof Import) {
                Aop.context().beanImport((Import) a1);
            } else {
                Aop.context().beanImport(a1.annotationType().getAnnotation(Import.class));
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
        if (global == null) {
            return;
        }

        _stopped = true;

        Utils.pools.submit(() -> {
            if (delay > 0) {
                Thread.sleep(delay);
            }

            global.prop().plugs().forEach(p -> p.stop());
            global = null;

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
     * 根据包加载bean
     * */
    public void beanScan(String basePackage){
        Aop.context().beanScan(basePackage);
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



    private final Router _router; //与函数同名，_开头
    /**
     * 路由器
     */
    public Router router() {
        return _router;
    }

    private final int _port; //端口
    private final SolonProps _prop; //属性配置
    private final Class<?> _source; //应用加载源

    protected Solon(Class<?> source, NvMap args) {
        _source = source;

        _prop = new SolonProps().load(args);
        _port = _prop.serverPort();

        //顺序不能换
        _router = new Router();

        _handler = new RouterHandler(_router);
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
    public SolonProps prop() {
        return _prop;
    }


    /**
     * 插入插件
     */
    public void plug(Plugin plugin) {
        PluginEntity p = new PluginEntity(plugin);
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
    public void before(String expr,  Handler handler) {
        before(expr, MethodType.ALL, handler);
    }

    public void before(String expr, MethodType method, Handler handler) {
        _router.add(expr, Endpoint.before, method, handler);
    }
    @Override
    public void before(String expr, MethodType method, int index, Handler handler) {
        _router.add(expr, Endpoint.before, method, index, handler);
    }

    /**
     * 重置监听
     */
    public void after(String expr, Handler handler) {
        after(expr, MethodType.ALL, handler);
    }

    public void after(String expr, MethodType method, Handler handler) {
        _router.add(expr, Endpoint.after, method, handler);
    }
    @Override
    public void after(String expr, MethodType method, int index, Handler handler) {
        _router.add(expr, Endpoint.after, method, index, handler);
    }

    /**
     * 主体监听
     */
    @Override
    public void add(String expr, MethodType method, Handler handler) {
        _router.add(expr, Endpoint.main, method, handler);
    }

    public void add(String expr, Class<?> clz) {
        BeanWrap bw = Aop.wrapAndPut(clz);
        if (bw != null) {
            new HandlerLoader(bw, expr).load(this);
        }
    }

    public void add(String expr, Class<?> clz, boolean remoting) {
        BeanWrap bw = Aop.wrapAndPut(clz);
        if (bw != null) {
            new HandlerLoader(bw, expr, remoting).load(this);
        }
    }


    /**
     * 添加所有方法监听
     */
    public void all(String path, Handler handler) {
        add(path, MethodType.ALL, handler);
    }

    /**
     * 添加HTTP所有方法的监听（GET,POST,PUT,PATCH,DELETE,HEAD）
     */
    public void http(String path, Handler handler) {
        add(path, MethodType.HTTP, handler);
    }

    /**
     * 添加GET方法的监听（REST.select 从服务端获取一或多项资源）
     */
    public void get(String path, Handler handler) {
        add(path, MethodType.GET, handler);
    }

    /**
     * 添加POST方法的监听（REST.create 在服务端新建一项资源）
     */
    public void post(String path, Handler handler) {
        add(path, MethodType.POST, handler);
    }

    /**
     * 添加PUT方法的监听（REST.update 客户端提供改变后的完整资源）
     */
    public void put(String path, Handler handler) {
        add(path, MethodType.PUT, handler);
    }

    /**
     * 添加PATCH方法的监听（REST.update 客户端提供改变的属性）
     */
    public void patch(String path, Handler handler) {
        add(path, MethodType.PATCH, handler);
    }

    /**
     * 添加DELETE方法的监听（REST.delete 从服务端删除资源）
     */
    public void delete(String path, Handler handler) {
        add(path, MethodType.DELETE, handler);
    }

    /**
     * 添加web socket方法的监听
     */
    public void ws(String path, Handler handler){
        add(path, MethodType.WEBSOCKET, handler);
    }

    /**
     * 添加web socket方法的监听
     */
    public void ws(String path, MessageListener listener){
        _router.add(path, MethodType.WEBSOCKET, listener);
    }

    /**
     * 添加socket方法的监听
     */
    public void socket(String path, Handler handler){
        add(path, MethodType.SOCKET, handler);
    }

    /**
     * 添加socket方法的监听
     */
    public void socket(String path, MessageListener listener){
        _router.add(path, MethodType.SOCKET, listener);
    }

    /**
     * Solon Handler
     */
    private Handler _handler = null;

    public Handler handlerGet() {
        return _handler;
    }

    public void handlerSet(Handler handler) {
        if (handler != null) {
            _handler = handler;
        }
    }

    /**
     * 统一代理入口
     */
    @Override
    public void handle(Context x) throws Throwable {
        if(_stopped){
            //停止后不再接收请求（避免产生脏数据）
            return;
        }

        try {
            //设置当前线程上下文
            ContextUtil.currentSet(x);
            _handler.handle(x);
        } catch (Throwable ex) {
            EventBus.push(ex);
            throw ex;
        } finally {
            //移除当前线程上下文
            ContextUtil.currentRemove();
        }
    }

    /*
    * 异常时，自动500处理
    * */
    public void tryHandle(Context x) {
        try {
            handle(x);
        } catch (Throwable ex) {
            ex = Utils.throwableUnwrap(ex);

            x.statusSet(500);
            x.setHandled(true);
            x.output(Utils.getFullStackTrace(ex));
        }
    }

    /**
     * 订阅异常事件
     * */
    public Solon onError(EventListener<Throwable> handler) {
        return onEvent(Throwable.class, handler);
    }

    /**
     * 订阅事件
     * */
    public <T> Solon onEvent(Class<T> type, EventListener<T> handler) {
        EventBus.subscribe(type, handler);
        return this;
    }

    /**
     * 启用Http信号接入
     * */
    private boolean _enableHttp = true; //与函数同名，_开头
    public boolean enableHttp(){
        return _enableHttp;
    }
    public Solon enableHttp(boolean enable){
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
    public Solon enableWebSocket(boolean enable){
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
    public Solon enableSocket(boolean enable){
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
    public Solon enableTransaction(boolean enable){
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
    public Solon enableCaching(boolean enable){
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
    public Solon enableStaticfiles(boolean enable){
        _enableStaticfiles = enable;
        return this;
    }

    /**
     * 启用会话状态
     * */
    private boolean _enableSessionState = true;
    public boolean enableSessionState(){
        return _enableSessionState;
    }
    public Solon enableSessionState(boolean enable){
        _enableSessionState = enable;
        return this;
    }
}
