package server;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.handle.MethodType;
import server.controller.ComplexModelService;
import server.dso.IComplexModelService;
import server.wrap.HessianHandler;
import server.dso.IGreetingService;

public class TmpApp {
    public static void main(String[] args) {
        Solon.start(TmpApp.class, args);

        Solon.global().before("**", MethodType.SOCKET,(ctx)->{
            ctx.headerMap().put("Content-Type", "application/protobuf");
            ctx.headerMap().put("X-Serialization","@protobuf");
        });

        Solon.global().add("/web/hessian", MethodType.HTTP,
                new HessianHandler(IGreetingService.class, Aop.get(IGreetingService.class)));


        Solon.global().add("/web/hessian_complex", MethodType.HTTP,
                new HessianHandler(IComplexModelService.class, Aop.get(ComplexModelService.class)));

        //HessianServlet
    }
}
