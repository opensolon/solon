import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.Aop;

/**
 * @author tomsun28
 */
@Controller
public class HelloApp {

    public static void main(String[] args) {
        Solon.start(HelloApp.class, args);

        SecurityManager securityManager = Aop.get(SecurityManager.class);
        SecurityUtils.setSecurityManager(securityManager);
    }


    @Mapping("/hello/{name}")
    public String hello(String name){
        Subject currentUser = SecurityUtils.getSubject();
        if (!currentUser.isAuthenticated()) {
            UsernamePasswordToken token = new UsernamePasswordToken("lonestarr", "vespa");
            token.setRememberMe(true);
            try {
                currentUser.login(token);
            } catch (UnknownAccountException uae) {
                return "There is no user with username of " + token.getPrincipal();
            } catch (IncorrectCredentialsException ice) {
                return "Password for account " + token.getPrincipal() + " was incorrect!";
            } catch (LockedAccountException lae) {
                return "The account for username " + token.getPrincipal() + " is locked.  " +
                        "Please contact your administrator to unlock it.";
            } catch (AuthenticationException ae) {
                return ae.getMessage();
            }
        }
        return pong(name);
    }

    @RequiresRoles("admin")
    public String pong(String name) {
        return "hello " + name + ". This is lisa.";
    }

}
