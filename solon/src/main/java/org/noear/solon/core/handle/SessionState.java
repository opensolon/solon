package org.noear.solon.core.handle;

/**
 * Session 状态器接口
 *
 * 用于对接http自带 sesssion 或 扩展 sesssion（可相互切换）
 *
 * @author noear
 * @since 1.0
 * */
public interface SessionState {
    /**
     * 刷新SESSION状态
     */
    default void sessionRefresh() {
    }

    /**
     * 发布SESSION状态(类似jwt)
     */
    default void sessionPublish() {
    }

    /**
     * 是否可替换
     */
    default boolean replaceable() {
        return true;
    }


    /**
     * 获取SESSION_ID
     */
    String sessionId();

    /**
     * 变更SESSION_ID
     */
    String sessionChangeId();

    /**
     * 获取SESSION状态
     */
    Object sessionGet(String key);

    /**
     * 设置SESSION状态
     */
    void sessionSet(String key, Object val);

    /**
     * 清除SESSION状态
     */
    void sessionClear();

    /**
     * 会话重置（清空数据，并变更会话ID）
     * */
    void sessionReset();

    /**
     * 获取会话令牌（如： solon.extend.sessionstate.jwt 插件支持）
     * */
    default String sessionToken() {
        throw new UnsupportedOperationException();
    }
}
