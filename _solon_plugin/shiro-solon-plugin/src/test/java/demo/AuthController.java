package demo;

import org.apache.shiro.authz.annotation.*;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

/**
 * @author noear 2021/5/6 created
 */
@Controller
public class AuthController {
    @RequiresAuthentication
    @Mapping("/t1")
    public String testAuthentication(){
        //验证用户是否登录，等同于方法subject.isAuthenticated() 结果为true时。

        return "OK";
    }
    @RequiresGuest
    @Mapping("/t2")
    public String testGuest(){
        /**
         * 验证是否是一个guest的请求，与@RequiresUser完全相反。
         * 换言之，RequiresUser  == !RequiresGuest。
         * 此时subject.getPrincipal() 结果为null.
         * */

        return "OK";
    }
    @RequiresUser
    @Mapping("/t3")
    public String testUser(){
        /**
         * 验证用户是否被记忆，user有两种含义：
         * 一种是成功登录的（subject.isAuthenticated() 结果为true）；
         * 另外一种是被记忆的（subject.isRemembered()结果为true）。
         * */

        return "OK";
    }
    @RequiresRoles("admin")
    @Mapping("/t4")
    public String testRoles(){
        //如果subject中有aRoleName角色才可以访问方法someMethod。如果没有这个权限则会抛出异常AuthorizationException。

        return "OK";
    }
    @RequiresPermissions({"file:read", "write:aFile.txt", "xx:xx:xx"} )
    @Mapping("/t5")
    public String xxx(){
        //要求subject中必须同时含有file:read和write:aFile.txt的权限才能执行方法someMethod()。否则抛出异常AuthorizationException。

        return "OK";
    }
}
