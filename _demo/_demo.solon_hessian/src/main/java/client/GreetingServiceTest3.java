package client;

import org.noear.fairy.Fairy;
import org.noear.fairy.decoder.HessionDecoder;
import org.noear.fairy.decoder.ProtobufDecoder;
import org.noear.fairy.encoder.SnackTypeEncoder;
import server.dso.IGreetingService;

public class GreetingServiceTest3 {
    public static void main(String[] args) throws Exception {
        //接口的动态代理工厂
        IGreetingService service = Fairy.builder()
                .encoder(SnackTypeEncoder.instance)
                .decoder(ProtobufDecoder.instance)
                .upstream(()->{
                    return "http://localhost:8080";
                }).create(IGreetingService.class);


        String result = service.greeting("tom");

        //远程方法调用
        System.out.println("hello(), " + result);
    }
}
