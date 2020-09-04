package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.annotation.XNote;

import java.util.*;

/**
 * 内部扩展桥接器
 * */
public class XBridge {
    //
    // SessionState 对接
    //
    private static XSessionState _sessionState = new XSessionStateDefault();
    private static boolean _sessionStateUpdated;

    /**
     * 设置Session状态管理器
     * */
    @XNote("设置Session状态管理器")
    public static void sessionStateSet(XSessionState ss) {
        if (ss != null) {
            _sessionState = ss;

            if (_sessionStateUpdated == false) {
                _sessionStateUpdated = true;

                XApp.global().before("**", XMethod.HTTP, (c) -> {
                    _sessionState.sessionRefresh();
                });
            }
        }
    }

    /**
     * 获取Session状态管理器
     * */
    @XNote("获取Session状态管理器")
    public static XSessionState sessionState() {
        return _sessionState;
    }

    static class XSessionStateDefault implements XSessionState {
        @Override
        public String sessionId() {
            return null;
        }

        @Override
        public Object sessionGet(String key) {
            return null;
        }

        @Override
        public void sessionSet(String key, Object val) {

        }
    }


    //
    // UpstreamFactory 对接
    //
    private static XUpstreamFactory _upstreamFactory = (service -> {
        throw new RuntimeException("XBridge: The upstream factory is not initialized");
    });

    /**
     * 获取负载工厂
     * */
    @XNote("获取负载工厂")
    public static XUpstreamFactory upstreamFactory() {
        return _upstreamFactory;
    }

    /**
     * 设置负载工厂
     * */
    @XNote("设置负载工厂")
    public static void upstreamFactorySet(XUpstreamFactory uf) {
        if (uf != null) {
            _upstreamFactory = uf;
        }
    }


    //
    // XActionExecutor 对接
    //

    /**
     * 动作默认执行器
     */
    private static XActionExecutor _actionExecutorDef = new XActionExecutorDefault();
    /**
     * 动作执行库
     */
    private static Set<XActionExecutor> _actionExecutors = new HashSet<>();

    /**
     * 获取默认的Action执行器
     * */
    @XNote("获取默认的Action执行器")
    public static XActionExecutor actionExecutorDef() {
        return _actionExecutorDef;
    }

    /**
     * 设置默认的Action执行器
     * */
    @XNote("设置默认的Action执行器")
    public static void actionExecutorDefSet(XActionExecutor ae) {
        if (ae != null) {
            _actionExecutorDef = ae;
        }
    }

    /**
     * 获取所有Action执行器
     * */
    @XNote("获取所有Action执行器")
    public static Set<XActionExecutor> actionExecutors() {
        return Collections.unmodifiableSet(_actionExecutors);
    }

    /**
     * 添加Action执行器
     */
    @XNote("添加Action执行器")
    public static void actionExecutorAdd(XActionExecutor e) {
        if (e != null) {
            _actionExecutors.add(e);
        }
    }


    //
    // XRender 对接
    //
    /**
     * 注册渲染器
     *
     * @param render 渲染器
     * */
    @XNote("注册渲染器")
    public static void renderRegister(XRender render) {
        if (render != null) {
            XRenderManager.register(render);
        }
    }

    /**
     * 印射渲染关系
     *
     * @param suffix 文件后缀名
     * @param render 渲染器
     * */
    @XNote("印射渲染关系")
    public static void renderMapping(String suffix, XRender render) {
        if (suffix != null && render != null) {
            XRenderManager.mapping(suffix, render);
        }
    }

    /**
     * 印射渲染关系
     *
     * @param suffix 文件后缀名
     * @param className 渲染器类名
     * */
    @XNote("印射渲染关系")
    public static void renderMapping(String suffix, String className) {
        if (suffix != null && className != null) {
            XRenderManager.mapping(suffix, className);
        }
    }


    //
    // XTranExecutor 对接
    //
    private static XTranExecutor _tranExecutor = (anno, runnable) -> {
        throw new RuntimeException("XBridge: The tran actuator is not initialized");
    };

    /**
     * 获取事务执行器
     * */
    @XNote("获取事务执行器")
    public static XTranExecutor tranExecutor() {
        return _tranExecutor;
    }

    /**
     * 设置事务执行器
     * */
    @XNote("设置事务执行器")
    public static void tranExecutorSet(XTranExecutor te) {
        if (te != null) {
            _tranExecutor = te;
        }
    }

    //
    // XCacheExecutor 对接
    //
    private static Map<String,CacheService> cacheServiceMap = new HashMap<>();


    /**
     * 缓存服务集合；只读
     * */
    @XNote("缓存服务集合；只读")
    public static Map<String, CacheService> cacheServiceMap() {
        return Collections.unmodifiableMap(cacheServiceMap);
    }

    /**
     * 添加缓存服务
     * */
    @XNote("添加缓存服务")
    public static void cacheServiceAdd(String name, CacheService cs){
        cacheServiceMap.put(name,cs);
    }

    @XNote("添加缓存服务")
    public static void cacheServiceAddIfAbsent(String name, CacheService cs){
        cacheServiceMap.putIfAbsent(name,cs);
    }

    /**
     * 获取缓存服务
     * */
    @XNote("获取缓存服务")
    public static CacheService cacheServiceGet(String name) {
        return cacheServiceMap.get(name);
    }

    private static XCacheExecutor _cacheExecutor = (anno, method, params, values, runnable) -> {
        throw new RuntimeException("XBridge: The cache actuator is not initialized");
    };

    /**
     * 获取缓存执行器
     * */
    @XNote("获取缓存执行器")
    public static XCacheExecutor cacheExecutor() {
        return _cacheExecutor;
    }

    /**
     * 设置缓存执行器
     * */
    @XNote("设置缓存执行器")
    public static void cacheExecutorSet(XCacheExecutor ce) {
        if (ce != null) {
            _cacheExecutor = ce;
        }
    }
}