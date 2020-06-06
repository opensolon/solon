package org.noear.solon;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;
import org.noear.solon.core.XHandlerAide;

import java.util.HashMap;
import java.util.Map;

/**
 * XHandler 导航类
 * */
public class XNav extends XHandlerAide implements XHandler {

    private final Map<String, XHandler> _main = new HashMap<>();
    private final String _path;

    public XNav() {
        XMapping _map = this.getClass().getAnnotation(XMapping.class);
        if (_map == null) {
            throw new RuntimeException("No XMapping!");
        }

        _path = _map.value();
    }

    /**
     * XMapping value
     * */
    protected String mapping(){
        return _path;
    }

    /**
     * 添加二级路径处理
     */
    public void add(String path, XHandler handler) {
        addDo(path, handler);
    }

    protected void addDo(String path, XHandler handler){
        //addPath 已处理 path1= null 的情况
        _main.put(XUtil.mergePath(_path, path).toUpperCase(), handler);
    }

    public XHandler get(XContext c, String path) {
        return _main.get(path);
    }

    @Override
    public void handle(XContext c) throws Throwable {
        XHandler m = get(c, c.pathAsUpper());

        if (m != null) {
            for (XHandler h : _before) {
                h.handle(c);
            }

            if (c.getHandled() == false) {
                m.handle(c);
            }

            for (XHandler h : _after) {
                h.handle(c);
            }
        } else {
            handle404(c);
        }
    }

    protected void handle404(XContext c) throws Throwable {
        c.status(404);
    }
}
