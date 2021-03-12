package org.noear.nami;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Nami 请求附件（放在Rpc请求的上一行，别太远）
 *
 * @author noear
 * @since 1.2
 */
public final class NamiAttachment {
    private Map<String, String> headers = new LinkedHashMap<>();
    private boolean autoRemove = true;

    /**
     * 设置头信息
     */
    public NamiAttachment headerSet(String name, String value) {
        if (value != null) {
            headers.put(name, value);
        }

        return this;
    }

    public boolean autoRemove(){
        return autoRemove;
    }

    public NamiAttachment autoRemove(boolean auto){
        autoRemove = auto;
        return this;
    }

    /**
     * 获取头信息
     */
    public Map<String, String> headers() {
        return headers;
    }

    private final static ThreadLocal<NamiAttachment> threadLocal = new InheritableThreadLocal<>();

    /**
     * 移除当前线程的上下文
     */
    public static void currentRemove() {
        threadLocal.remove();
    }

    /**
     * 获取当前线程的上下文
     */
    public static NamiAttachment currentGet() {
        return threadLocal.get();
    }

    /**
     * 获取当前线程的上下文，或自动创建
     */
    public static NamiAttachment current() {
        NamiAttachment tmp = threadLocal.get();
        if (tmp == null) {
            tmp = new NamiAttachment();
            threadLocal.set(tmp);
        }

        return tmp;
    }
}
