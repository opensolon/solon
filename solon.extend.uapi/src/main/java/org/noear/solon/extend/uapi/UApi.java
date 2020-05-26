package org.noear.solon.extend.uapi;

import org.noear.solon.core.XContext;

public interface UApi {
    /** 是否使用缓存 */
    default boolean isUsingCache(){return false;}

    /** 缓存时间（s） */
    default int cacheSeconds(){return 60*5;}

    /** 接口名（即：代号） */
    String name();

    /** 调用 */
    Object call(XContext cxt);
}
