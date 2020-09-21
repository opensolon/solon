package client;

import org.noear.solonclient.XProxy;
import org.noear.solonclient.serializer.HessionSerializerD;
import org.noear.solonclient.serializer.SnackSerializerD;
import server.dso.IGreetingService;

public class GreetingServiceTest2 {
    public static void main(String[] args) throws Exception {
        //接口的动态代理工厂
        IGreetingService service = new XProxy()
                .serializer(SnackSerializerD.instance_type)
                .deserializer(HessionSerializerD.instance)
                .filterAdd((p,h,a)->{
                    h.put("Solon-Serialization","@hession");
                })
                .upstream(()->{
                    return "http://localhost:8080";
                }).create(IGreetingService.class);


        String result = service.greeting("tom");

        //远程方法调用
        System.out.println("hello(), " + result);
    }
}
