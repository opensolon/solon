package client;

import com.caucho.hessian.client.HessianProxyFactory;
import org.noear.solonclient.Enctype;
import org.noear.solonclient.XProxy;
import org.noear.solonclient.serializer.HessionSerializer;
import org.noear.solonclient.serializer.SnackSerializer;
import server.dso.IComplexModelService;
import server.dso.IGreetingService;

public class GreetingServiceTest2 {
    public static void main(String[] args) throws Exception {
        //RPC访问地址
        XProxy.defaultSerializer = SnackSerializer.instance_type;
        XProxy.defaultDeserializer = HessionSerializer.instance;
        XProxy.defaultEnctype = Enctype.application_json;

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
