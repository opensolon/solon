package webapp.controller;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import webapp.model.User;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Mapping("users")
@Controller
public class UserController {
    @Mapping(value = "get1")
    public String get1(String name) {
        return name.toUpperCase();
    }

    @Mapping(value = "get2")
    public void get2(String name, Context ctx) throws Throwable{
        User user = new User();

        user.setName(name);
        user.setSex(1);
        user.setIcon("fa-btn");
        user.setState(true);
        user.setRegTime(new Date());
        user.setOrderList(Arrays.asList("a","1","#"));

        Map<String,Object> attrs = new HashMap<>();
        attrs.put("a","1");
        attrs.put("b",2);
        user.setAttrMap(attrs);

        ctx.remotingSet(true);
        ctx.render(user);
        ctx.remotingSet(false);
    }

    @Mapping(value = "get3")
    public Object get3(String name, Context ctx) throws Throwable{
        User user = new User();

        user.setName(name);
        user.setSex(1);
        user.setIcon("fa-btn");
        user.setState(true);
        user.setRegTime(new Date());
        user.setOrderList(Arrays.asList("a","1","#"));

        Map<String,Object> attrs = new HashMap<>();
        attrs.put("a","1");
        attrs.put("b",2);
        user.setAttrMap(attrs);

        return user;
    }
}