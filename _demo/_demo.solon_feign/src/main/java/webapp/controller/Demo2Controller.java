package webapp.controller;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XInject;
import org.noear.solon.annotation.XMapping;
import webapp.dso.RemoteService;
import webapp.model.User;

@XMapping("demo2")
@XController
public class Demo2Controller {
    @XInject
    RemoteService service;


    @XMapping("test")
    public String test() {
        String result = service.getOwner("scott");

        return result;
    }

    @XMapping("test2")
    public Object test2() {
        User user = service.getOwner2("scott");

        return user;
    }
}
