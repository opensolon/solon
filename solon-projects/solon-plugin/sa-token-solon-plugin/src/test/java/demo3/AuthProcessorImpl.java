package demo3;

import org.noear.solon.auth.AuthFailureHandler;
import org.noear.solon.auth.AuthProcessor;
import org.noear.solon.auth.annotation.Logical;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

/**
 * @author noear 2022/7/14 created
 */
public class AuthProcessorImpl implements AuthProcessor, AuthFailureHandler {
    @Override
    public boolean verifyIp(String ip) {
        return false;
    }

    @Override
    public boolean verifyLogined() {
        String path = Context.current().pathNew();

        if(path.startsWith("/manager/")){
            return AuthHelperForManager.STPLOGIC.isLogin();
        }else{
            return AuthHelperForUser.STPLOGIC.isLogin();
        }
    }

    @Override
    public boolean verifyPath(String path, String method) {
        return false;
    }

    @Override
    public boolean verifyPermissions(String[] permissions, Logical logical) {
        String path = Context.current().pathNew();

        if(path.startsWith("/manager/")){
            if (Logical.AND == logical) {
                return AuthHelperForManager.STPLOGIC.hasPermissionAnd(permissions);
            } else {
                return AuthHelperForManager.STPLOGIC.hasPermissionOr(permissions);
            }
        }

        return false;
    }

    @Override
    public boolean verifyRoles(String[] roles, Logical logical) {
        String path = Context.current().pathNew();

        if(path.startsWith("/manager/")){
            if (Logical.AND == logical) {
                return AuthHelperForManager.STPLOGIC.hasRoleAnd(roles);
            } else {
                return AuthHelperForManager.STPLOGIC.hasRoleOr(roles);
            }
        }

        return false;
    }

    @Override
    public void onFailure(Context ctx, Result rst) throws Throwable {
        String path = Context.current().pathNew();

//        if(path.startsWith("/manager/")){
//            if(rst.getCode() == 403) {
//                ExceptionHelper.render(ResponseStatus.FAILURE.getCode(), "暂无权限");
//            } else {
//                ExceptionHelper.render(ResponseStatus.FAILURE.getCode(), rst.getDescription());
//            }
//        }else{
//            ExceptionHelper.render(ResponseStatus.FAILURE.getCode(), rst.getDescription());
//        }
    }
}
