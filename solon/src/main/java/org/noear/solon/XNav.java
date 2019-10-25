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

    protected Map<String,XHandler> _main = new HashMap<>();

    public XNav() {
        XMapping _map = this.getClass().getAnnotation(XMapping.class);
        if (_map == null) {
            throw new RuntimeException("No XMapping!");
        }

        _path = _map.value();
    }

    private String _path = null;

    /** 添加二级路径处理 */
    public void add(String path, XHandler handler) {
        //addPath 已处理 path1= null 的情况
        _main.put(XUtil.mergePath(_path, path).toUpperCase(), handler);

    }

    @Override
    public void handle(XContext c) throws Throwable {
        XHandler m = _main.get(c.pathAsUpper());

        if(m != null) {
            for (XHandler h : _before) {
                h.handle(c);
            }

            if(c.getHandled()==false) {
                m.handle(c);
            }

            for (XHandler h : _after) {
                h.handle(c);
            }
        }else{
            c.status(404);
        }
    }
}
