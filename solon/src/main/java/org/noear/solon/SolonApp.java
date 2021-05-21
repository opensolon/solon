package org.noear.solon;

import org.noear.solon.core.event.*;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.route.Router;
import org.noear.solon.core.route.RouterDefault;
import org.noear.solon.core.route.RouterHandler;
import org.noear.solon.annotation.Import;
import org.noear.solon.core.Aop;
import org.noear.solon.core.*;
import org.noear.solon.core.message.Listener;

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
public class SolonApp implements HandlerSlots {
    /**
     * 初始化（不能合在构建函数里）
     * */
    protected void init() {

        List<ClassLoader> loaderList;

        //1.尝试加载扩展文件夹
        String filterStr = cfg().extendFilter();
        if (Utils.isEmpty(filterStr)) {
            //不需要过滤
            loaderList = ExtendLoader.load(cfg().extend(), false);
        } else {
            //增加过滤
            String[] filterS = filterStr.split(",");
            loaderList = ExtendLoader.load(cfg().extend(), false, (path) -> {
                for (String f : filterS) {
                    if (path.contains(f)) {
                        return true;
                    }
                }

                return false;
            });
        }


        //2.尝试扫描插件
        cfg().plugsScan(loaderList);
    }

    /**
     * 运行应用
     * */
    protected void run() {
        //event::0.x.推送App init end事件
        EventBus.push(AppInitEndEvent.instance);

        //1.1.尝试启动插件（顺序不能乱） //不能用forEach，以免当中有插进来
        List<PluginEntity> plugs = cfg().plugs();
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
        NvMap map = cfg().getXmap("solon.view.mapping");
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




    //////////////////////////////////

    private final Set<Signal> signals = new LinkedHashSet<>();


    /**
     * 添加信号
     * */
    public void signalAdd(Signal instance){
        signals.add(instance);
    }

    /**
     * 获取信号记录
     * */
    public Collection<Signal> signals(){
        return Collections.unmodifiableSet(signals);
    }


    //////////////////////////////////


    /**
     * 共享变量（一般用于插件之间）
     * */
    private final Set<BiConsumer<String,Object>> _onSharedAdd_event=new HashSet<>();
    private final Map<String,Object> _shared=new HashMap<>();
    private Map<String,Object> _shared_unmod;

    /**
     * 获取类加载器
     * */
    public ClassLoader classLoader(){
        return JarClassLoader.global();
    }

    /**
     * 根据源扫描bean
     * */
    public void beanScan(Class<?> source){
        Aop.context().beanScan(source);
    }

    /**
     * 根据包扫描bean
     * */
    public void beanScan(String basePackage){
        Aop.context().beanScan(basePackage);
    }

    /**
     * 根据类型构建bean
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




    private RouterHandler _routerHandler;
    private Router _router; //与函数同名，_开头
    /**
     * 路由器
     */
    public Router router() {
        return _router;
    }
    public void routerSet(Router router) {
        if (router != null) {
            _router = router;
            _routerHandler.bind(router);
        }
    }

    private final int _port; //端口
    private final SolonProps _prop; //属性配置
    private final Class<?> _source; //应用加载源
    private final long _startupTime;

    private FilterChainNode _filterChain;
    protected boolean stopped = false;

    protected SolonApp(Class<?> source, NvMap args) {
        _startupTime = System.currentTimeMillis();
        _source = source;

        _prop = new SolonProps().load(source, args);
        _port = _prop.serverPort();

        //顺序不能换
        _router = new RouterDefault();
        _routerHandler = new RouterHandler(_router);
        _filterChain = new FilterChainNode(this::doFilter);

        _handler = _routerHandler;

        enableJarIsolation(_prop.getBool("solon.extend.isolation", false));
    }

    protected long elapsedTimes(){
        return System.currentTimeMillis() - _startupTime;
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
    public SolonProps cfg() {
        return _prop;
    }


    /**
     * 插入插件
     */
    public void plug(Plugin plugin) {
        PluginEntity p = new PluginEntity(plugin);
        p.start();
        cfg().plugs().add(p);
    }

    /**
     * 添加插件（只有执行前添加才有效）
     * @param priority 优先级（越大越优化）
     * @param plugin 插件
     * */
    public void pluginAdd(int priority, Plugin plugin) {
        PluginEntity p = new PluginEntity(plugin, priority);
        cfg().plugs().add(p);
        cfg().plugsSort();
    }

    ///////////////////////////////////////////////
    //
    // 以下为web handler 有关
    //
    //////////////////////////////////////////////

    /**
     * 添加过滤器（按先进后出策略执行）
     *
     * @param filter 过滤器
     * */
    public void filter(Filter filter) {
        FilterChainNode tmp = new FilterChainNode(filter);
        tmp.next = _filterChain;
        _filterChain = tmp;
    }

    /**
     * 前置监听
     */
    public void before(Handler handler) {
        before("**", MethodType.ALL, handler);
    }

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
    public void after(Handler handler) {
        after("**", MethodType.ALL, handler);
    }

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
    public void ws(String path, Listener listener){
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
    public void socket(String path, Listener listener){
        _router.add(path, MethodType.SOCKET, listener);
    }

    /**
     * 添加监听
     * */
    public void listen(String path, Listener listener) {
        _router.add(path, MethodType.ALL, listener);
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
    protected void doHandle(Context x) throws Throwable {
        try {
            //设置当前线程上下文
            ContextUtil.currentSet(x);
            _filterChain.doFilter(x);
        } catch (Throwable ex) {
            EventBus.push(ex);
            throw ex;
        } finally {
            //移除当前线程上下文
            ContextUtil.currentRemove();
        }
    }

    protected void doFilter(Context ctx, FilterChain chain) throws Throwable{
        _handler.handle(ctx);
    }

    /*
    * 异常时，自动500处理
    * */
    public void tryHandle(Context x) {
        try {
            if (stopped) {
                x.statusSet(403);
            } else {
                doHandle(x);
            }
        } catch (Throwable ex) {
            ex = Utils.throwableUnwrap(ex);

            x.statusSet(500);
            x.setHandled(true);

            if (Solon.cfg().isDebugMode() || Solon.cfg().isFilesMode()) {
                x.output(Utils.getFullStackTrace(ex));
            }
        }
    }

    /**
     * 订阅事件
     * */
    public <T> SolonApp onEvent(Class<T> type, EventListener<T> handler) {
        EventBus.subscribe(type, handler);
        return this;
    }

    /**
     * 订阅异常事件
     * */
    public SolonApp onError(EventListener<Throwable> handler) {
        return onEvent(Throwable.class, handler);
    }

    /**
     * 启用Http信号接入
     * */
    private boolean _enableHttp = true; //与函数同名，_开头
    public boolean enableHttp(){
        return _enableHttp;
    }
    public SolonApp enableHttp(boolean enable){
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
    public SolonApp enableWebSocket(boolean enable){
        _enableWebSocket = enable;
        return this;
    }
    /**
     * 启用WebSocket as socketd 信号接入
     * */
    private boolean _enableWebSocketD = false;
    public boolean enableWebSocketD(){
        return _enableWebSocketD;
    }
    public SolonApp enableWebSocketD(boolean enable) {
        _enableWebSocketD = enable;
        if (enable) {
            _enableWebSocket = enable;
        }
        return this;
    }
    /**
     * 启用Socket as sockted 信号接入
     * */
    private boolean _enableSocketD = false;
    public boolean enableSocketD(){
        return _enableSocketD;
    }
    public SolonApp enableSocketD(boolean enable){
        _enableSocketD = enable;
        return this;
    }
    /**
     * 启用事务
     * */
    private boolean _enableTransaction = true;
    public boolean enableTransaction(){
        return _enableTransaction;
    }
    public SolonApp enableTransaction(boolean enable){
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
    public SolonApp enableCaching(boolean enable){
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
    public SolonApp enableStaticfiles(boolean enable){
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
    public SolonApp enableSessionState(boolean enable){
        _enableSessionState = enable;
        return this;
    }

    /**
     * 启用扩展Jar隔离
     * */
    private boolean _enableJarIsolation = false;
    public boolean enableJarIsolation(){
        return _enableJarIsolation;
    }
    private SolonApp enableJarIsolation(boolean enable){
        _enableJarIsolation = enable;
        return this;
    }

    private boolean _enableSafeStop = false;
    public boolean enableSafeStop() {
        return _enableSafeStop;
    }
    /**
     * 启用安全停止
     * */
    public void enableSafeStop(boolean enable){
        _enableSafeStop = enable;
    }
}
