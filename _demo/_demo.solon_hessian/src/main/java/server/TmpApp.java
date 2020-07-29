package server;

import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XMethod;
import server.wrap.HessianSolonHandler;
import server.dso.IGreetingService;

public class TmpApp {
    public static void main(String[] args) {
        XApp.start(TmpApp.class, args);

        XApp.global().add("/web/hessian", XMethod.HTTP, new HessianSolonHandler(IGreetingService.class, Aop.get(IGreetingService.class)));


    }
}
