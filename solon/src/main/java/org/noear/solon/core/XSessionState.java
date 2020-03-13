package org.noear.solon.core;

public interface XSessionState {
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

    default int priority(){return 0;}
}
