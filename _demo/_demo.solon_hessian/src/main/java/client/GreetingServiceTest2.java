package client;

import org.noear.nami.Nami;
import org.noear.nami.coder.hessian.HessianDecoder;
import org.noear.nami.coder.jackson.JacksonEncoder;
import org.noear.solon.Solon;
import server.dso.IGreetingService;

public class GreetingServiceTest2 {
    public static void main(String[] args) throws Exception {
        Solon.start(ClientApp.class, args, app->app.enableHttp(false));

        //接口的动态代理工厂
        IGreetingService service = Nami.builder()
                .encoder(JacksonEncoder.instance)
                .decoder(HessianDecoder.instance)
                .upstream(()-> "http://localhost:8080")
                .create(IGreetingService.class);


        String result = service.greeting("tom");

        //远程方法调用
        System.out.println("hello(), " + result);
    }
}
