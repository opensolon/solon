package org.noear.solon.core.handle;

/**
 * @author noear 2021/2/14 created
 */
public interface SessionStateFactory {
    /** 优先级 */
    default int priority(){return 0;}

    SessionState create(Context ctx);
}
