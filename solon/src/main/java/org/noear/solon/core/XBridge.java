package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.annotation.XNote;

import java.util.*;

/**
 * 内部扩展桥接器
 *
 * @author noear
 * @since 1.0
 * */
public class XBridge {
    //
    // SessionState 对接
    //
    private static XSessionState sessionState = new XSessionStateDefault();
    private static boolean sessionStateUpdated;

    /**
     * 设置Session状态管理器
     */
    @XNote("设置Session状态管理器")
    public static void sessionStateSet(XSessionState ss) {
        if (ss != null) {
            sessionState = ss;

            if (sessionStateUpdated == false) {
                sessionStateUpdated = true;

                XApp.global().before("**", XMethod.HTTP, (c) -> {
                    sessionState.sessionRefresh();
                });
            }
        }
    }

    /**
     * 获取Session状态管理器
     */
    @XNote("获取Session状态管理器")
    public static XSessionState sessionState() {
        return sessionState;
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
    private static XUpstream.Factory upstreamFactory = null;

    /**
     * 获取负载工厂
     */
    @XNote("获取负载工厂")
    public static XUpstream.Factory upstreamFactory() {
        return upstreamFactory;
    }

    /**
     * 设置负载工厂
     */
    @XNote("设置负载工厂")
    public static void upstreamFactorySet(XUpstream.Factory uf) {
        if (uf != null) {
            upstreamFactory = uf;
        }
    }


    //
    // XActionExecutor 对接
    //

    /**
     * 动作默认执行器
     */
    private static XActionExecutor actionExecutorDef = new XActionExecutorDefault();
    /**
     * 动作执行库
     */
    private static Set<XActionExecutor> actionExecutors = new HashSet<>();

    /**
     * 获取默认的Action执行器
     */
    @XNote("获取默认的Action执行器")
    public static XActionExecutor actionExecutorDef() {
        return actionExecutorDef;
    }

    /**
     * 设置默认的Action执行器
     */
    @XNote("设置默认的Action执行器")
    public static void actionExecutorDefSet(XActionExecutor ae) {
        if (ae != null) {
            actionExecutorDef = ae;
        }
    }

    /**
     * 获取所有Action执行器
     */
    @XNote("获取所有Action执行器")
    public static Set<XActionExecutor> actionExecutors() {
        return Collections.unmodifiableSet(actionExecutors);
    }

    /**
     * 添加Action执行器
     */
    @XNote("添加Action执行器")
    public static void actionExecutorAdd(XActionExecutor e) {
        if (e != null) {
            actionExecutors.add(e);
        }
    }


    //
    // XRender 对接
    //

    /**
     * 注册渲染器
     *
     * @param render 渲染器
     */
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
     */
    @XNote("印射渲染关系")
    public static void renderMapping(String suffix, XRender render) {
        if (suffix != null && render != null) {
            XRenderManager.mapping(suffix, render);
        }
    }

    /**
     * 印射渲染关系
     *
     * @param suffix    文件后缀名
     * @param className 渲染器类名
     */
    @XNote("印射渲染关系")
    public static void renderMapping(String suffix, String className) {
        if (suffix != null && className != null) {
            XRenderManager.mapping(suffix, className);
        }
    }


    //
    // XTranExecutor 对接
    //
    private static XTranExecutor tranExecutor = () -> false;

    /**
     * 获取事务执行器
     */
    @XNote("获取事务执行器")
    public static XTranExecutor tranExecutor() {
        return tranExecutor;
    }

    /**
     * 设置事务执行器
     */
    @XNote("设置事务执行器")
    public static void tranExecutorSet(XTranExecutor te) {
        if (te != null) {
            tranExecutor = te;
        }
    }

}