package org.noear.solon.core.handler;

/**
 * Session 状态器接口
 *
 * 用于对接http自带 sesssion 或 扩展 sesssion（可相互切换）
 *
 * @author noear
 * @since 1.0
 * */
public interface SessionState {
    /** 刷新SESSION状态 */
    default void sessionRefresh(){}

    /** 优先级 */
    default int priority(){return 0;}

    /** 是否可替换 */
    default boolean replaceable(){
        return true;
    }


    /** 获取SESSION_ID */
    String sessionId();

    /** 获取SESSION状态 */
    Object sessionGet(String key);

    /** 设置SESSION状态 */
    void sessionSet(String key, Object val);

    /** 清除SESSION状态 */
    default void sessionClear(){}


}
