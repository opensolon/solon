package org.noear.solon.core.handle;


import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.FactoryManager;
import org.noear.solon.core.event.EventBus;

/**
 * 上下文状态处理工具（独立出来，可为别的业务服务）
 *
 * @see SolonApp#tryHandle(Context)
 * @author noear
 * @since 1.0
 * */
public class ContextUtil {

    public static final String contentTypeDef = "text/plain;charset=UTF-8";

    private final static ThreadLocal<Context> threadLocal = FactoryManager.getGlobal().newThreadLocal(ContextUtil.class, false);

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

        //发布事件，方便线程状态清理
        EventBus.publish(ContextRemovedEvent.instance);
    }

    /**
     * 获取当前线程的上下文
     * */
    public static Context current(){
        Context tmp = threadLocal.get();

        if (tmp == null && Solon.cfg().testing()) {
            tmp = new ContextEmpty();
            threadLocal.set(tmp);
        }

        return tmp;
    }
}
