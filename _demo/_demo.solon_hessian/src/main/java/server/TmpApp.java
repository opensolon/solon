package server;

import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XMethod;
import server.controller.ComplexModelService;
import server.dso.IComplexModelService;
import server.wrap.HessianHandler;
import server.dso.IGreetingService;

public class TmpApp {
    public static void main(String[] args) {
        XApp.start(TmpApp.class, args);

        XApp.global().before("**",XMethod.SOCKET,(ctx)->{
            ctx.headerMap().put("Content-Type", "application/protobuf");
        });

        XApp.global().add("/web/hessian", XMethod.HTTP,
                new HessianHandler(IGreetingService.class, Aop.get(IGreetingService.class)));


        XApp.global().add("/web/hessian_complex", XMethod.HTTP,
                new HessianHandler(IComplexModelService.class, Aop.get(ComplexModelService.class)));

        //HessianServlet
    }
}
