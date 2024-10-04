package demo.server;


import org.noear.solon.Solon;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;

public class ServerTest {
    public static void main(String[] args) {
        Solon.start(ServerTest.class, args);
    }

    @BindingType(SOAPBinding.SOAP12HTTP_BINDING)
    @WebService(serviceName = "HelloService", targetNamespace = "http://demo.solon.io")
    public static class HelloServiceImpl {
        public String hello(String name) {
            return "hello " + name;
        }
    }
}
