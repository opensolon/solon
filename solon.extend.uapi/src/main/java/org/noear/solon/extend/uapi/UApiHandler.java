package org.noear.solon.extend.uapi;

import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;

public class UApiHandler implements UApi {
    private String _name;
    private XHandler _hander;

    public UApiHandler(String name, XHandler handler) {
        _name = name;
        _hander = handler;
    }

    @Override
    public void handle(XContext c) throws Throwable {
        //
        // handler 需要自己写入 c.attr["result"]
        //
        _hander.handle(c);
    }

    @Override
    public String name() {
        return _name;
    }
}
