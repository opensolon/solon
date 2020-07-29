package client;

import com.caucho.hessian.client.HessianProxyFactory;
import server.dso.IGreetingService;

public class GreetingServiceTest {
    public static void main(String[] args) throws Exception {
        //RPC访问地址
        String url = "http://localhost:8080/web/hessian";

        //接口的动态代理工厂
        HessianProxyFactory factory = new HessianProxyFactory();
        IGreetingService service = (IGreetingService) factory.create(IGreetingService.class, url);

        String result = service.greeting("tom");

        //远程方法调用
        System.out.println("hello(), " + result);
    }
}
