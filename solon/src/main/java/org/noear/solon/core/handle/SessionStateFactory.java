package org.noear.solon.core.handle;

/**
 * Session 状态器工厂
 *
 * @author noear
 * @since 1.3
 */
public interface SessionStateFactory {
    /**
     * 优先级
     * */
    default int priority(){return 0;}

    /**
     * 创建会话状态
     * */
    SessionState create(Context ctx);
}
