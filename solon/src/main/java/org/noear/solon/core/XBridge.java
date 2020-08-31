package org.noear.solon.core;

import org.noear.solon.XApp;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 内部扩展桥接器
 * */
public class XBridge {
    //
    // SessionState 对接
    //
    private static XSessionState _sessionState = new XSessionStateDefault();
    private static boolean _sessionStateUpdated;

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
        throw new RuntimeException("Uninitialized upstreamFactory");
    });

    public static XUpstreamFactory upstreamFactory() {
        return _upstreamFactory;
    }

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

    public static XActionExecutor actionExecutorDef() {
        return _actionExecutorDef;
    }

    public static void actionExecutorDefSet(XActionExecutor ae) {
        if (ae != null) {
            _actionExecutorDef = ae;
        }
    }

    public static Set<XActionExecutor> actionExecutors() {
        return Collections.unmodifiableSet(_actionExecutors);
    }

    /**
     * 添加动作执行器
     */
    public static void actionExecutorAdd(XActionExecutor e) {
        if (e != null) {
            _actionExecutors.add(e);
        }
    }


    //
    // XRender 对接
    //
    public static void renderRegister(XRender render) {
        if (render != null) {
            XRenderManager.register(render);
        }
    }

    public static void renderMapping(String suffix, XRender render) {
        if (suffix != null && render != null) {
            XRenderManager.mapping(suffix, render);
        }
    }

    public static void renderMapping(String suffix, String className) {
        if (suffix != null && className != null) {
            XRenderManager.mapping(suffix, className);
        }
    }


    //
    // TranExecutor 对接
    //
    private static TranFactory _tranFactory;
    private static TranExecutor _tranExecutor = (anno, runnable) -> {
        runnable.run();
    };

    public static TranExecutor tranExecutor() {
        return _tranExecutor;
    }

    public static void tranExecutorSet(TranExecutor te) {
        if (te != null) {
            _tranExecutor = te;
        }
    }

    public static TranFactory tranFactory() {
        return _tranFactory;
    }

    public static void tranFactorySet(TranFactory tf) {
        if (tf != null) {
            _tranFactory = tf;
        }
    }
}