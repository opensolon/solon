package webapp.controller;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.extend.feign.FeignClient;
import webapp.dso.RemoteService3;
import webapp.model.User;

@Mapping("demo3")
@Controller
public class Demo3Controller {
    @FeignClient(name = "user-service", path = "/users/")
    RemoteService3 service;

    @Inject
    RemoteService3 service2;

    @Mapping("test")
    public String test() {
        String result = service.getOwner("scott");

        return result;
    }

    @Mapping("test2")
    public Object test2() {
        User user = service2.get2("scott");

        return user;
    }
}
