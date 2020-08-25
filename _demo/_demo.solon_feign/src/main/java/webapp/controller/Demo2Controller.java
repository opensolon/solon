package webapp.controller;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XInject;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.extend.feign.FeignClient;
import webapp.dso.RemoteService;
import webapp.model.User;

@XMapping("demo2")
@XController
public class Demo2Controller {
    @FeignClient(url = "http://127.0.0.1:8080/users/")
    RemoteService service;

    @XInject
    RemoteService service2;

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
