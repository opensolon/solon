package webapp.controller;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;
import webapp.model.UserModel;

@Controller
public class HelloworldController {
    @Mapping("/helloworld")
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
