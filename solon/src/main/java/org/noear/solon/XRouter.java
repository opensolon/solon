package org.noear.solon;

import org.noear.solon.core.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 通用路由器
 * */
public class XRouter {
    private final XListenerList[] _list = {
            new XListenerList(), //before:0
            new XListenerList(), //main
            new XListenerList(), //after:2

            new XListenerList(), //at before:3
            new XListenerList(),
            new XListenerList()}; //at after:5

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
        XListener xl = new XListener(path, method, index, handler);

        if (endpoint != XEndpoint.main && "@@".equals(path)) {
            endpoint += 3;

            XListenerList tmp = new XListenerList(_list[endpoint]);
            tmp.add(xl);
            tmp.sort(Comparator.comparing(l -> l.index));
            _list[endpoint] = tmp;
        } else {
            _list[endpoint].add(xl);
        }
    }

    /**
     * 清空路由关系
     */
    public void clear() {
        _list[0].clear();
        _list[1].clear();
        _list[2].clear();

        _list[3].clear();
        _list[4].clear();
        _list[5].clear();
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

        return _list[endpoint].stream()
                .filter(l -> l.matches(method, path))
                .sorted(Comparator.comparingInt(l -> l.index))
                .collect(Collectors.toList());
    }

    public List<XListener> atBefore() {
        return Collections.unmodifiableList(_list[3]);
    }

    public List<XListener> atAfter() {
        return Collections.unmodifiableList(_list[5]);
    }

    public static class XListenerList extends ArrayList<XListener> {
        public XListenerList(){
            super();
        }

        public XListenerList(Collection<XListener> coll){
            super(coll);
        }
    }
}
