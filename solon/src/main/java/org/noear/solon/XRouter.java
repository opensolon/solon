package org.noear.solon;

import org.noear.solon.core.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通用路由器
 * */
public class XRouter {
    private final XListenerList[] _list = {new XListenerList(), new XListenerList(), new XListenerList()};

    /**
     * 添加路由关系
     */
    public void add(String path, XHandler handler) {
        add(path, XEndpoint.main, XMethod.HTTP, handler);
    }

    /**
     * 添加路由关系
     */
    public void add(String path, int endpoint, XMethod method, XHandler handler) {
        add(path, endpoint, method, 0, handler);
    }

    /**
     * 添加路由关系
     */
    public void add(String path, int endpoint, XMethod method, int index, XHandler handler) {
        _list[endpoint].add(new XListener(path, method, index, handler));
    }

    /**
     * 清空路由关系
     */
    public void clear() {
        _list[0].clear();
        _list[1].clear();
        _list[2].clear();
    }

    /**
     * 区配一个目标（根据上上文）
     */
    public XHandler matchOne(XContext context, int endpoint) {
        String path = context.path();
        XMethod method = XMethod.valueOf(context.method());

        for (XListener l : _list[endpoint]) {
            if (l.matches(method, path)) {
                return l.handler;
            }
        }

        return null;
    }

    /**
     * 区配多个目标（根据上上文）
     */
    public List<XHandler> matchAll(XContext context, int endpoint) {
        String path = context.path();
        XMethod method = XMethod.valueOf(context.method());

        return matchAllDo(path, method, endpoint);
    }

    private List<XHandler> matchAllDo(String path, XMethod method, int endpoint) {
        return _list[endpoint].stream()
                .filter(l -> l.matches(method, path))
                .sorted(Comparator.comparingInt(l -> l.index))
                .map(l -> l.handler)
                .collect(Collectors.toList());
    }

    public List<XHandler> atBefore() {
        return matchAllDo("@@", XMethod.ALL, XEndpoint.before);
    }

    public List<XHandler> atAfter() {
        return matchAllDo("@@", XMethod.ALL, XEndpoint.after);
    }

    public static class XListenerList extends ArrayList<XListener> {

    }
}
