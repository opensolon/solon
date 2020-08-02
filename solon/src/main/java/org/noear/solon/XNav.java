package org.noear.solon;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XEndpoint;
import org.noear.solon.core.XHandler;
import org.noear.solon.core.XHandlerAide;

import java.util.HashMap;
import java.util.Map;

/**
 * XHandler 导航类
 * */
public class XNav extends XHandlerAide {

    private final Map<String, XHandler> _main = new HashMap<>();
    private final String _path;

    public XNav() {
        this(null);
    }

    public XNav(XMapping mapping) {
        if (mapping == null) {
            mapping = this.getClass().getAnnotation(XMapping.class);
        }

        if (mapping == null) {
            throw new RuntimeException("No XMapping!");
        }

        _path = mapping.value();
    }

    /**
     * XMapping value
     */
    public String mapping() {
        return _path;
    }

    /**
     * 添加二级路径处理
     */
    public void add(String path, XHandler handler) {
        addDo(path, handler);
    }

    protected void addDo(String path, XHandler handler) {
        //addPath 已处理 path1= null 的情况
        _main.put(XUtil.mergePath(_path, path).toUpperCase(), handler);
    }

    /**
     * 获取接口
     */
    public final XHandler get(String path) {
        if (path == null) {
            return null;
        } else {
            return _main.get(path.toUpperCase());
        }
    }

    /**
     * 查找接口
     * <p>
     * 用于被人重写，从而控制find过程
     */
    protected XHandler findDo(XContext c, String path) {
        return get(path);
    }

    @Override
    public void handle(XContext c) throws Throwable {
        XHandler m = findDo(c, c.pathAsUpper());

        if (m != null) {
            for (XHandler h : _before) {
                handleDo(c, h, XEndpoint.before);
            }

            if (c.getHandled() == false) {
                handleDo(c, m, XEndpoint.main);
            }

            for (XHandler h : _after) {
                handleDo(c, h, XEndpoint.after);
            }
        } else {
            handle404(c);
        }
    }

    protected void handleDo(XContext c, XHandler h, int endpoint) throws Throwable {
        h.handle(c);
    }

    protected void handle404(XContext c) throws Throwable {
        c.status(404);
    }
}
