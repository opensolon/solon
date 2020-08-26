package webapp.controller;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XInject;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.extend.feign.FeignClient;
import webapp.dso.RemoteService2;
import webapp.dso.RemoteService3;
import webapp.model.User;

@XMapping("demo3")
@XController
public class Demo3Controller {
    @FeignClient(name = "user-service", path = "/users/")
    RemoteService3 service;

    @XInject
    RemoteService3 service2;

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
