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
package org.noear.solon;

import org.noear.solon.annotation.Import;
import org.noear.solon.core.*;
import org.noear.solon.core.convert.ConverterManager;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.core.event.*;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.route.RouterWrapper;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.serialize.SerializerManager;
import org.noear.solon.core.util.ConsumerEx;
import org.noear.solon.core.util.RunUtil;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 应用
 *
 * <pre>{@code
 * @SolonMain
 * public class DemoApp{
 *     public static void main(String[] args){
 *         Solon.start(DemoApp.class, args);
 *     }
 * }
 * }</pre>
 *
 * @author noear
 * @since 1.0
 */
public class SolonApp extends RouterWrapper {
    static final Logger log = LoggerFactory.getLogger(SolonApp.class);

    private final SolonProps _cfg; //属性配置
    private final AppClassLoader _classLoader;
    private final AppContext _context;//容器上下文
    private final ConverterManager _converterManager; //转换管理器
    private final SerializerManager _serializerManager; //渲染管理器
    private final RenderManager _renderManager; //渲染管理器
    private final HandlerPipeline _handler = new HandlerPipeline();

    private final Class<?> _source; //应用加载源
    private final URL _sourceLocation;
    private final long _startupTime;

    private boolean stopping = false;

    /**
     * 应用上下文
     */
    @Override
    public AppContext context() {
        return _context;
    }

    /**
     * 转换管理器
     */
    public ConverterManager converterManager() { //预计 v4.0 后标为弃用
        return converters();
    }

    /**
     * 转换管理器
     *
     * @since 3.6
     */
    public ConverterManager converters() {
        return _converterManager;
    }

    /**
     * 序列化管理器
     */
    public SerializerManager serializerManager() { //预计 v4.0 后标为弃用
        return serializers();
    }

    /**
     * 序列化管理器
     *
     * @since 3.6
     */
    public SerializerManager serializers() {
        return _serializerManager;
    }

    /**
     * 渲染管理器
     */
    public RenderManager renderManager() { //预计 v4.0 后标为弃用
        return renders();
    }

    /**
     * 渲染管理器
     *
     * @since 3.6
     */
    public RenderManager renders() {
        return _renderManager;
    }

    /**
     * 工厂管理器
     */
    public FactoryManager factoryManager() { //预计 v4.0 后标为弃用
        return factories();
    }

    /**
     * 工厂管理器
     *
     * @since 3.6
     */
    public FactoryManager factories() {
        return FactoryManager.getGlobal();
    }

    /**
     * 应用属性（或配置）
     */
    public SolonProps cfg() {
        return _cfg;
    }

    /**
     * 是否为主应用对象
     */
    protected boolean isMain() {
        return true;
    }

    protected SolonApp(Class<?> source, NvMap args) throws Exception {
        //添加启动类检测
        if (source == null) {
            throw new IllegalArgumentException("The startup class parameter('source') cannot be null");
        }

        //添加启动类包名检测
        if (source.getPackage() == null || Utils.isEmpty(source.getPackage().getName())) {
            throw new IllegalStateException("The startup class is missing package: " + source.getName());
        }

        _startupTime = System.currentTimeMillis();
        _source = source;
        _classLoader = new AppClassLoader(AppClassLoader.global());
        _sourceLocation = source.getProtectionDomain().getCodeSource().getLocation();
        _converterManager = new ConverterManager();
        _serializerManager = new SerializerManager();
        _renderManager = new RenderManager();


        //初始化配置
        _cfg = new SolonProps(this, args);
        _context = new AppContext(this, _classLoader, _cfg);
        _enableScanning = ("0".equals(args.get("scanning")) == false); //不等于0，则启用扫描

        //初始化路由
        initRouter(this);

        _handler.next(routerHandler());
    }


    /**
     * 启动
     */
    protected void startDo(ConsumerEx<SolonApp> initialize) throws Throwable {
        //1.0.打印构造时的告警
        if (_cfg.warns.size() > 0) {
            for (String warn : _cfg.warns) {
                log.warn(warn);
            }
        }

        //2.0.内部初始化等待（尝试ping等待）
        initAwait();

        //2.1.内部初始化（如配置等，顺序不能乱）
        init(initialize);

        //2.2.配置表达式修订
        cfg().revise();

        //3.运行应用（运行插件、扫描Bean等）
        run();
    }

    /**
     * 预停止
     */
    protected void prestopDo() {
        this.cfg().plugins().forEach(p -> p.prestop());
        this.context().prestop();
        EventBus.publishTry(new AppPrestopEndEvent(this));
    }

    protected void stoppingDo() {
        this.stopping = true;
    }

    /**
     * 停止
     */
    protected void stopDo() {
        this.cfg().plugins().forEach(p -> p.stop());
        this.context().stop();
        EventBus.publishTry(new AppStopEndEvent(this));
        RunUtil.shutdown();
    }

    /**
     * 初始化等待
     */
    private void initAwait() throws Throwable {
        String addr = cfg().get("solon.start.ping");

        if (Utils.isNotEmpty(addr)) {
            try {
                while (true) {
                    if (Utils.ping(addr)) {
                        log.info("Start ping succeed: " + addr);
                        Thread.sleep(1000); //成功也再等1s
                        break;
                    } else {
                        log.warn("Start ping failure: " + addr);
                        Thread.sleep(2000);
                    }
                }
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * 初始化（不能合在构建函数里）
     */
    private void init(ConsumerEx<SolonApp> initialize) throws Throwable {
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

        //2.运行自定义初始化（可能会有插件排除）
        if (initialize != null) {
            initialize.accept(this);
        }

        //3.尝试扫描插件
        cfg().pluginScan(loaderList);
    }


    /**
     * 运行应用
     */
    private void run() throws Throwable {

        //event::0.x.推送App init end事件
        EventBus.publish(new AppInitEndEvent(this));

        List<PluginEntity> plugs = cfg().plugins();
        //1.0.尝式初始化插件 //一般插件不需要
        for (int i = 0, len = plugs.size(); i < len; i++) {
            if (log.isDebugEnabled()) {
                log.debug("Plugin init: " + plugs.get(i).getClassName());
            }
            plugs.get(i).init(context());
        }

        //event::1.0.x推送Plugin init end事件
        EventBus.publish(new AppPluginInitEndEvent(this));

        log.debug("Plugin start");

        //1.1.尝试启动插件（顺序不能乱） //不能用forEach，以免当中有插进来
        for (int i = 0, len = plugs.size(); i < len; i++) {
            plugs.get(i).start(context());
        }

        //1.2.检查配置是否完全适配
        cfg().complete();

        //event::1.3.推送Plugin load end事件
        EventBus.publish(new AppPluginLoadEndEvent(this));


        if (enableScanning()) {
            log.debug("Scanning start");
        }

        //2.1.通过注解导入bean（一般是些配置器）
        beanImportTry();

        //2.2.通过源扫描bean
        if (source() != null && enableScanning()) {
            context().beanScan(source());
        }

        //event::2.x.推送Bean load end事件
        EventBus.publish(new AppBeanLoadEndEvent(this));

        //3.加载渲染关系
        Map<String, String> map = cfg().getMap("solon.view.mapping.");
        map.forEach((k, v) -> {
            renders().register("." + k, v);
        });

        //3.1.尝试设置 context-path
        if (Utils.isNotEmpty(this.cfg().serverContextPath())) {
            filterIfAbsent(Constants.FT_IDX_CONTEXT_PATH, new ContextPathFilter());
        }

        log.debug("AppContext start");

        //3.2.标识上下文加载完成
        context().start();

        //event::4.x.推送App load end事件
        EventBus.publish(new AppLoadEndEvent(this));
    }

    //通过注解，导入bean
    protected void beanImportTry() {
        if (_source == null) {
            return;
        }

        for (Annotation a1 : _source.getAnnotations()) {
            if (a1 instanceof Import) {
                context().beanImport((Import) a1);
            } else {
                context().beanImport(a1.annotationType().getAnnotation(Import.class));
            }
        }
    }


    /// ///////////////////////////////

    private final Map<Integer, Signal> signals = new LinkedHashMap<>();


    /**
     * 添加信号
     */
    public void signalAdd(Signal instance) {
        signals.putIfAbsent(instance.port(), instance);
    }

    /**
     * 获取信号
     *
     * @param port 端口
     */
    public Signal signalGet(int port) {
        return signals.get(port);
    }

    /**
     * 获取信号记录
     */
    public Collection<Signal> signals() {
        return Collections.unmodifiableCollection(signals.values());
    }


    //////////////////////////////////


    /**
     * 共享变量（一般用于插件之间）
     */
    private final Set<BiConsumer<String, Object>> _onSharedAdd_event = new HashSet<>();
    private final Map<String, Object> _shared = new HashMap<>();
    private Map<String, Object> _shared_unmod;

    /**
     * 类加载器
     */
    public AppClassLoader classLoader() {
        return _classLoader;
    }

    /**
     * 添加共享对象
     */
    public void sharedAdd(String key, Object obj) {
        _shared.put(key, obj);
        _onSharedAdd_event.forEach(fun -> {
            fun.accept(key, obj);
        });
    }

    /**
     * 获取共享对象（异步获取）
     */
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
     */
    public void onSharedAdd(BiConsumer<String, Object> event) {
        _onSharedAdd_event.add(event);
    }

    /**
     * 共享对象
     */
    public Map<String, Object> shared() {
        if (_shared_unmod == null) {
            _shared_unmod = Collections.unmodifiableMap(_shared);
        }

        return _shared_unmod;
    }


    /**
     * 从启动开启已运行时间
     */
    protected long elapsedTimes() {
        return System.currentTimeMillis() - _startupTime;
    }


    /**
     * 启动入口类
     */
    public Class<?> source() {
        return _source;
    }

    /**
     * 启动入口类所在位置
     */
    public URL sourceLocation() {
        return _sourceLocation;
    }


    /**
     * 插入插件（一般用于动态加载，比如 faas）
     */
    public void plug(Plugin plugin) {
        PluginEntity p = new PluginEntity(plugin);
        p.init(context());
        p.start(context());
        cfg().plugins().add(p);
    }

    /**
     * 添加插件（只有执行前添加才有效）
     *
     * @param priority 优先级（越大越优化）
     * @param plugin   插件
     */
    public void pluginAdd(int priority, Plugin plugin) {
        PluginEntity p = new PluginEntity(plugin, priority);
        cfg().plugins().add(p);
        Collections.sort(cfg().plugins());
    }

    /**
     * 获取插件
     *
     * @param clazz 插件类
     */
    public PluginEntity pluginGet(Class<?> clazz) {
        for (PluginEntity pe : cfg().plugins()) {
            if (clazz.isInstance(pe.getPlugin())) {
                return pe;
            }
        }

        return null;
    }

    /**
     * 插件排除
     *
     * @since 3.0
     */
    public void pluginExclude(Class<?> pluginClz) {
        pluginExclude(pluginClz.getName());
    }

    /**
     * 插件排除
     *
     * @since 3.0
     */
    public void pluginExclude(String pluginClzName) {
        cfg().pluginExclude(pluginClzName);
    }


    /**
     * 处理器获取
     */
    public HandlerPipeline handler() {
        return _handler;
    }


    /**
     * 应用请求处理入口(异常时，自动500处理)
     */
    public void tryHandle(Context x) {
        try {
            //设置当前线程上下文
            ContextHolder.currentSet(x);

            if (stopping) {
                x.status(503);
            } else {
                chains().doFilter(x, _handler);

                //todo: 改由 HttpException 处理（不会到这里来了）
//                if (x.getHandled() == false) { //@since: 1.9
//                    if (x.status() <= 200 && x.mainHandler() == null) {//@since: 1.10
//                        int statusPreview = x.statusPreview(); //支持405  //@since: 2.5
//                        if (statusPreview > 0) {
//                            x.status(statusPreview);
//                        } else {
//                            x.status(404);
//                        }
//                    }
//                    //x.setHandled(true);  //todo: 不能加，对websocket有影响
//                }
            }

            //40x,50x...
            doStatus(x);
        } catch (Throwable ex) {
            ex = Utils.throwableUnwrap(ex);

            //如果未处理，尝试处理
            if (ex instanceof StatusException) {
                StatusException se = (StatusException) ex;
                x.status(se.getCode());

                if (se.getCode() != 404 && se.getCode() != 405) {
                    log.warn("SolonApp tryHandle failed, code=" + se.getCode(), ex);
                }
            } else {
                //推送异常事件 //todo: Action -> Gateway? -> RouterHandler -> Filter -> SolonApp!
                log.warn("SolonApp tryHandle failed!", ex);

                x.status(500);
            }

            //如果未渲染，尝试渲染
            if (x.getRendered() == false) {
                //40x,50x...
                try {
                    if (doStatus(x) == false) {
                        if (this.cfg().isDebugMode()) {
                            x.output(ex);
                        }
                    }
                } catch (RuntimeException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            //移除当前线程上下文
            ContextHolder.currentRemove();
        }
    }

    protected boolean doStatus(Context x) throws Throwable {
        if (x.status() >= 400 && _statusHandlers.size() > 0) {
            Handler h = _statusHandlers.get(x.status());
            if (h != null) {
                x.status(200);
                x.setHandled(true);
                h.handle(x);
                return true;
            }
        }

        return false;
    }

    /**
     * 订阅事件
     */
    public <T> SolonApp onEvent(Class<T> type, EventListener<T> handler) {
        EventBus.subscribe(type, handler);
        return this;
    }

    /**
     * 订阅事件
     */
    public <T> SolonApp onEvent(Class<T> type, int index, EventListener<T> handler) {
        EventBus.subscribe(type, index, handler);
        return this;
    }

    private Map<Integer, Handler> _statusHandlers = new HashMap<>();

    /**
     * 订阅异常状态
     */
    public SolonApp onStatus(Integer code, Handler handler) {
        _statusHandlers.put(code, handler);
        return this;
    }


    /**
     * 锁住线程（如果有需要，建议在启动程序的最后调用）
     */
    public void block() throws InterruptedException {
        Thread.currentThread().join();
    }


    private boolean _enableHttp = true; //与函数同名，_开头

    /**
     * 是否已启用 Http 信号接入
     */
    public boolean enableHttp() {
        return _enableHttp && NativeDetector.isNotAotRuntime();
    }

    /**
     * 启用 Http 信号接入
     */
    public SolonApp enableHttp(boolean enable) {
        _enableHttp = enable;
        return this;
    }

    private boolean _enableWebSocket = false;

    public boolean enableWebSocket() {
        return _enableWebSocket && NativeDetector.isNotAotRuntime();
    }

    /**
     * 启用 WebSocket 信号接入
     *
     * @param enable 是否启用
     */
    public SolonApp enableWebSocket(boolean enable) {
        _enableWebSocket = enable;
        return this;
    }


    private boolean _enableSocketD = false;

    /**
     * 是否已启用 SocketD 信号接入
     */
    public boolean enableSocketD() {
        return _enableSocketD && NativeDetector.isNotAotRuntime();
    }

    /**
     * 启用 SocketD 信号接入
     *
     * @param enable 是否启用
     */
    public SolonApp enableSocketD(boolean enable) {
        _enableSocketD = enable;
        return this;
    }


    private boolean _enableTransaction = true;

    /**
     * 是否已启用事务
     */
    public boolean enableTransaction() {
        return _enableTransaction;
    }

    /**
     * 启用事务
     *
     * @param enable 是否启用
     */
    public SolonApp enableTransaction(boolean enable) {
        _enableTransaction = enable;
        return this;
    }

    private boolean _enableCaching = true;

    /**
     * 是否已启用缓存
     */
    public boolean enableCaching() {
        return _enableCaching;
    }

    /**
     * 启用缓存
     *
     * @param enable 是否启用
     */
    public SolonApp enableCaching(boolean enable) {
        _enableCaching = enable;
        return this;
    }

    private boolean _enableScanning = true;

    /**
     * 是否已启用扫描
     */
    public boolean enableScanning() {
        //都为 true 则为 true
        return _enableScanning;
    }

    /**
     * 启用扫描
     *
     * @param enable 是否启用
     */
    public SolonApp enableScanning(boolean enable) {
        _enableScanning = enable;
        return this;
    }

    private boolean _enableStaticfiles = true;

    /**
     * 是否已启用静态文件服务
     */
    public boolean enableStaticfiles() {
        return _enableStaticfiles;
    }

    /**
     * 启用静态文件服务
     *
     * @param enable 是否启用
     */
    public SolonApp enableStaticfiles(boolean enable) {
        _enableStaticfiles = enable;
        return this;
    }


    private boolean _enableSessionState = true;

    /**
     * 是否已启用会话状态
     */
    public boolean enableSessionState() {
        return _enableSessionState;
    }

    /**
     * 启用会话状态
     *
     * @param enable 是否启用
     */
    public SolonApp enableSessionState(boolean enable) {
        _enableSessionState = enable;
        return this;
    }
}