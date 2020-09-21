package client;

import org.noear.fairy.Fairy;
import org.noear.fairy.decoder.HessionDecoder;
import org.noear.fairy.encoder.SnackEncoder;
import server.dso.IGreetingService;

public class GreetingServiceTest2 {
    public static void main(String[] args) throws Exception {
        //接口的动态代理工厂
        IGreetingService service = new Fairy()
                .encoder(SnackEncoder.instance_type)
                .decoder(HessionDecoder.instance)
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
