package client;

import org.noear.solonclient.Enctype;
import org.noear.solonclient.XProxy;
import org.noear.solonclient.serializer.HessionSerializerD;
import org.noear.solonclient.serializer.SnackSerializerD;
import server.dso.IGreetingService;

public class GreetingServiceTest2 {
    public static void main(String[] args) throws Exception {
        //RPC访问地址
        XProxy.defaultSerializer = SnackSerializerD.instance_type;
        XProxy.defaultDeserializer = HessionSerializerD.instance;

        //接口的动态代理工厂
        IGreetingService service = new XProxy()
                .headerAdd("Solon-Serialization","@hession")
                .upstream((name)->{
                    return "http://localhost:8080";
                }).create(IGreetingService.class);


        String result = service.greeting("tom");

        //远程方法调用
        System.out.println("hello(), " + result);
    }
}
