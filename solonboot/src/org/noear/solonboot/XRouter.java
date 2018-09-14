package org.noear.solonboot;

import com.sun.istack.internal.NotNull;
import org.noear.solonboot.protocol.XContext;

/*通用路由*/
public interface XRouter<T> {
    void add(@NotNull String path, T handler);

    void add(@NotNull String path, int type, String method, T handler);

    T matched(XContext context);

    T matched(XContext context, int type);
}
