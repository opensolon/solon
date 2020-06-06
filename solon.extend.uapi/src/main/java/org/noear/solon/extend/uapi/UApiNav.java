package org.noear.solon.extend.uapi;

import org.noear.solon.XNav;
import org.noear.solon.XUtil;
import org.noear.solon.core.*;

/**
 * UAPI导航控制器
 *
 * 1.提供容器，重新组织Handler运行
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
        XContextUtil.currentRemove();
        XContextUtil.currentSet(c2);

        //调用父级处理
        super.handle(c2);
    }

    /**
     * 添加接口
     */
    public void add(Class<?> clz) {
        BeanWrap bw = Aop.wrap(clz);
        UApiBeanWebWrap uw = new UApiBeanWebWrap(bw);

        uw.load((path, m, h) -> {
            UApi api = null;
            if (h instanceof UApi) {
                api = (UApi) h;
            } else {
                api = new UApiHandler(path, h);
            }

            if (XUtil.isEmpty(api.name())) {
                _def = api;
            } else {
                addDo(api.name(), api);
            }
        });
    }

    /**
     * 添加接口
     */
    @Override
    public void add(String path, XHandler handler) {
        UApi api = new UApiHandler(path, handler);
        add(api.name(), api);
    }

    @Override
    public XHandler get(XContext c, String path) {
        UApi api = (UApi) super.get(c, path);

        if (api == null) {
            if (_def != null) {
                try {
                    _def.handle(c);
                } catch (Throwable ex) {
                    throw new RuntimeException(ex);
                }
            }
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
