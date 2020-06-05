package org.noear.solon.extend.uapi;

import org.noear.solon.XNav;
import org.noear.solon.XUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XContextUtil;
import org.noear.solon.core.XHandler;

/**
 * UAPI导航控制器
 * */
public abstract class UApiNav extends XNav {
    private XHandler _def;
    public UApiNav() {
        super();

        register();
    }

    /**
     * 注册二级代理
     */
    protected abstract void register();


    @Override
    public void handle(XContext c) throws Throwable {
        //转换上下文
        //
        XContext c2 = context(c);
        XContextUtil.currentSet(c2);

        //调用父级处理
        super.handle(c2);
    }

    /**
     * 添加接口
     * */
    public void add(UApi api) {
        if(XUtil.isEmpty(api.name())){
            _def = api;
        }else {
            addDo(api.name(), api);
        }
    }

    /**
     * 添加接口
     * */
    @Override
    public void add(String path, XHandler handler) {
        throw new RuntimeException("No supported");
    }

    @Override
    public XHandler get(XContext c, String path) {
        UApi api = (UApi) super.get(c, path);

        c.attrSet("_uapinav","1");

        if (api == null) {
            c.setHandled(true);
            return _def;
        } else {
            c.attrSet("api", api.name());
            return api;
        }
    }

    /**
     * 提供上下文转换机制
     */
    public XContext context(XContext ctx) {
        return ctx;
    }
}
