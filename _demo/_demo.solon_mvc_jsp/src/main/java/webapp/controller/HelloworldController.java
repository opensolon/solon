package webapp.controller;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XInject;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.ModelAndView;
import org.noear.solon.core.XContext;
import webapp.model.UserModel;

@XController
public class HelloworldController {

    @XInject("${custom.user}")
    protected String user;

    @XMapping("/helloworld")
    public Object helloworld(XContext ctx){
        UserModel m = new UserModel();
        m.id=10;
        m.name = "刘之西东";
        m.sex=1;

        ModelAndView vm = new ModelAndView("helloworld.jsp");

        vm.put("title","demo");
        vm.put("message","hello world!");

        vm.put("m",m);

        vm.put("user", user);

        vm.put("ctx",ctx);

        return vm;
    }
}
