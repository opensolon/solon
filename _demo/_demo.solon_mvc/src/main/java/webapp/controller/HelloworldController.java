package webapp.controller;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.ModelAndView;
import webapp.model.UserModel;

@XController
public class HelloworldController {
    @XMapping("/helloworld")
    public Object helloworld(){
        UserModel m = new UserModel();
        m.id=10;
        m.name = "刘之西东";
        m.sex=1;

        ModelAndView vm = new ModelAndView("helloworld.ftl");

        vm.put("title","demo");
        vm.put("message","hello world!");
        vm.put("m",m);

        return vm;
    }
}
