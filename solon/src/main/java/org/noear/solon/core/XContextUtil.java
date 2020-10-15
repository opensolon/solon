package org.noear.solon.core;


/**
 * 上下文状态处理工具（独立出来，可为别的业务服务）
 *
 * @author noear
 * @since 1.0
 * */
public class XContextUtil {

    public static final String contentTypeDef = "text/plain;charset=UTF-8";

    private final static ThreadLocal<XContext> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的上下文
     * */
    public static void currentSet(XContext context){
        threadLocal.set(context);
    }

    /**
     * 移除当前线程的上下文
     * */
    public static void currentRemove(){
        threadLocal.remove();
    }

    /**
     * 获取当前线程的上下文
     * */
    public static XContext current(){
        return threadLocal.get();
    }
}
