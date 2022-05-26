package demo;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.noear.solon.annotation.Component;

/**
 * @author noear 2021/5/6 created
 */
public class AuthorizingRealmImpl extends AuthorizingRealm {
    /**
     * 验证用户权限
     * */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // TODO Auto-generated method stub
//        User user = (User)principals.getPrimaryPrincipal();
//        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//        info.addStringPermission(permission);
//        return info;
        return null;
    }

    /**
     * 验证用户登录信息
     * */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // TODO Auto-generated method stub
//        String name = String.valueOf(token.getPrincipal());
//        String password = new String((byte[])token.getCredentials());
//        User user = userService.login(name);
//        return new SimpleAuthenticationInfo(user , user.getPassword(),getName());
        return null;
    }
}
