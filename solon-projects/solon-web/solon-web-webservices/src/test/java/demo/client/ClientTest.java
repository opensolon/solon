package demo.client;


import org.noear.solon.web.webservices.WebServiceHelper;

import javax.jws.WebMethod;
import javax.jws.WebService;

public class ClientTest {
    public static void main(String[] args) {
        String wsAddress = "http://localhost:8080/ws/HelloService";
        HelloService helloService = WebServiceHelper.createWebClient(wsAddress, HelloService.class);

        System.out.println("rst::" + helloService.hello("noear"));
    }

    @WebService(serviceName = "HelloService", targetNamespace = "http://demo.solon.io")
    public interface HelloService {
        @WebMethod
        String hello(String name);
    }
}
