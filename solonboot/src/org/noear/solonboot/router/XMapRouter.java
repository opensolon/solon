package org.noear.solonboot.router;

import com.sun.istack.internal.NotNull;
import org.noear.solonboot.XRouter;
import org.noear.solonboot.protocol.XContext;
import org.noear.solonboot.protocol.XEndpoint;
import org.noear.solonboot.protocol.XMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/*印射路由,功能弱性能好*/
public class XMapRouter<T> implements XRouter<T> {
    private Map<String, T> routerList = new HashMap<>();

    ///////////////////////////////////////////////

    @Override
    public void add(@NotNull String path, T handler) {
        routerList.put(path.toUpperCase(), handler);
    }

    @Override
    public void add(String path, int type, String method, T handler) {
        add(path,handler);
    }


    @Override
    public T matched(XContext context) {
        return routerList.get(context.pathAsUpper());
    }

    @Override
    public T matched(XContext context, int type) {
        return matched(context);
    }
}
