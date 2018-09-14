package org.noear.solonboot;

import org.noear.solonboot.protocol.*;
import org.noear.solonboot.router.XMapRouter;

public abstract class XNavigation implements XHandlerEx {
    protected XRouter<XHandler> _router;

    public XNavigation(){
        _router = router();
        register();
    }

    public XRouter<XHandler> router(){
        return new XMapRouter<>();
    }

    public abstract void register();

    public void add(String path, XHandler handler) {
        for (String p : path()) {
            _router.add(XUtil.addPath(p, path), handler);
        }
    }

    @Override
    public String[] method() {
        return new String[]{XMethod.ALL};
    }

    @Override
    public void handle(XContext context) throws Exception {
        XHandler handler = _router.matched(context, XEndpoint.main);

        if (handler != null) {
            handler.handle(context);
        }
    }
}
