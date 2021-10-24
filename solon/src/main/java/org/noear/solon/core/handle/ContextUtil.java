package org.noear.solon.core.handle;


import org.noear.solon.SolonApp;

/**
 * 上下文状态处理工具（独立出来，可为别的业务服务）
 *
 * @see SolonApp#handle(Context)
 * @author noear
 * @since 1.0
 * */
public class ContextUtil {

    public static final String contentTypeDef = "text/plain;charset=UTF-8";

    private final static ThreadLocal<Context> threadLocal = new InheritableThreadLocal<>();

    /**
     * 设置当前线程的上下文
     * */
    public static void currentSet(Context context){
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
    public static Context current(){
        return threadLocal.get();
    }
}
