package webapp.controller;

import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import webapp.dso.RemoteService;
import webapp.model.User;

@XMapping("demo")
@XController
public class DemoController {
    RemoteService service = Feign.builder()
            .options(new Request.Options(1000, 3500))
            .retryer(new Retryer.Default(5000, 5000, 3))
            .target(RemoteService.class, "http://127.0.0.1:8080");

    RemoteService service2 = Feign.builder()
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .options(new Request.Options(1000, 3500))
            .retryer(new Retryer.Default(5000, 5000, 3))
            .target(RemoteService.class, "http://127.0.0.1:8080");

    @XMapping("test")
    public String test() {
        String result = service.getOwner("scott");

        return result;
    }

    @XMapping("test2")
    public Object test2() {
        User user = service2.get2("scott");

        return user;
    }
}
