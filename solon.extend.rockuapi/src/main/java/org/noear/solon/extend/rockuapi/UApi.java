package org.noear.solon.extend.rockuapi;

import org.noear.solon.core.XContext;

import java.io.Serializable;

public interface UApi {
    /** 是否使用缓存 */
    boolean isUsingCache();

    /** 缓存时间（s） */
    int cacheSeconds();

    /** 接口名（即：代号） */
    String name();

    /** 调用 */
    Object call(XContext cxt);
}
