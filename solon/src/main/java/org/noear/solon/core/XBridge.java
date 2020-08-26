package org.noear.solon.core;

import org.noear.solon.XApp;

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
        _upstreamFactory = uf;
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
        _actionExecutorDef = ae;
    }

    public static Set<XActionExecutor> actionExecutors() {
        return _actionExecutors;
    }

    /**
     * 添加动作执行器
     */
    public static void actionExecutorAdd(XActionExecutor executor) {
        _actionExecutors.add(executor);
    }


    //
    // XRender 对接
    //
    public static void renderRegister(XRender render) {
        XRenderManager.register(render);
    }

    public static void renderMapping(String suffix, XRender render) {
        XRenderManager.mapping(suffix, render);
    }

    public static void renderMapping(String suffix, String className) {
        XRenderManager.mapping(suffix, className);
    }
}
