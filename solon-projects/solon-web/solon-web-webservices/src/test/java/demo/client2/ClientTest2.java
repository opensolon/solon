package demo.client2;


import org.noear.solon.Solon;
import org.noear.solon.annotation.Managed;
import org.noear.solon.core.bean.LifecycleBean;
import org.noear.solon.web.webservices.WebServiceReference;

import javax.jws.WebMethod;
import javax.jws.WebService;

public class ClientTest2 {
    public static void main(String[] args) {
        Solon.start(ClientTest2.class, args, app -> app.enableHttp(false));
    }

    @Managed
    public static class DemoCom implements LifecycleBean {
        @WebServiceReference("http://localhost:8080/ws/HelloService")
        private HelloService helloService;

        @Override
        public void start() throws Throwable {
            System.out.println("rst::" + helloService.hello("noear"));
        }
    }

    @WebService(serviceName = "HelloService", targetNamespace = "http://demo.solon.io")
    public interface HelloService {
        @WebMethod
        String hello(String name);
    }
}
