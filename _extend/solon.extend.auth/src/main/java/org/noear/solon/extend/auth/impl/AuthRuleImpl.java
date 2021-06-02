package org.noear.solon.extend.auth.impl;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.core.util.PathAnalyzer;
import org.noear.solon.extend.auth.AuthFailureHandler;
import org.noear.solon.extend.auth.AuthRule;
import org.noear.solon.extend.auth.AuthUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 授权规则默认实现
 *
 * @author noear
 * @since 1.4
 */
public class AuthRuleImpl implements AuthRule {
    /**
     * 拦截规则（包函规则）
     */
    private List<PathAnalyzer> includeList = new ArrayList<>();
    /**
     * 放行规则（排除规则）
     */
    private List<PathAnalyzer> excludeList = new ArrayList<>();
    private boolean verifyIp;
    private boolean verifyLogined;
    private boolean verifyPath;
    private String[] verifyPermissions;
    private boolean verifyPermissionsAnd;
    private String[] verifyRoles;
    private boolean verifyRolesAnd;
    private AuthFailureHandler failureHandler;

    @Override
    public AuthRule include(String pattern) {
        includeList.add(PathAnalyzer.get(pattern));
        return this;
    }

    @Override
    public AuthRule exclude(String pattern) {
        excludeList.add(PathAnalyzer.get(pattern));
        return this;
    }

    @Override
    public AuthRule verifyIp() {
        verifyIp = true;
        return this;
    }

    @Override
    public AuthRule verifyLogined() {
        verifyLogined = true;
        return this;
    }

    @Override
    public AuthRule verifyPath() {
        verifyPath = true;
        verifyLogined = true;
        return this;
    }

    @Override
    public AuthRule verifyPermissions(String... permissions) {
        verifyPermissions = permissions;
        verifyPermissionsAnd = false;
        verifyLogined = true;
        return this;
    }

    @Override
    public AuthRule verifyPermissionsAnd(String... permissions) {
        verifyPermissions = permissions;
        verifyPermissionsAnd = true;
        verifyLogined = true;
        return this;
    }

    @Override
    public AuthRule verifyRoles(String... roles) {
        verifyRoles = roles;
        verifyRolesAnd = false;
        verifyLogined = true;
        return this;
    }

    @Override
    public AuthRule verifyRolesAnd(String... roles) {
        verifyRoles = roles;
        verifyRolesAnd = true;
        verifyLogined = true;
        return this;
    }

    @Override
    public AuthRule failure(AuthFailureHandler handler) {
        failureHandler = handler;
        return this;
    }

    public boolean test(String path) {
        //1.放行匹配
        for (PathAnalyzer pa : excludeList) {
            if (pa.matches(path)) {
                return false;
            }
        }

        //2.拦截匹配
        for (PathAnalyzer pa : includeList) {
            if (pa.matches(path)) {
                return true;
            }
        }

        //3.无匹配
        return false;
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        String path = ctx.pathNew().toLowerCase();

        if (test(path) == false) {
            return;
        }

        //
        //Ip验证
        //
        if (verifyIp) {
            //验证登录情况
            String ip = ctx.realIp();
            if (AuthUtil.verifyIp(ip) == false) {
                //验证失败的
                failureDo(ctx, Result.failure(403, ip + AuthUtil.MESSAGE_OF_IP));
                return;
            }
        }

        //
        //登录验证
        //
        if (verifyLogined) {
            //验证登录情况
            if (AuthUtil.verifyLogined() == false) {
                //未登录的，跳到登录页
                if (AuthUtil.adapter().loginUrl() == null) {
                    failureDo(ctx, Result.failure(401, AuthUtil.MESSAGE_OF_LOGINED));
                } else {
                    ctx.redirect(AuthUtil.adapter().loginUrl());
                    ctx.setHandled(true);
                }
                return;
            }
        }

        //
        //路径验证
        //
        if (verifyPath) {
            //验证路径与方式权限
            if (AuthUtil.verifyPath(path, ctx.method()) == false) {
                //验证失败的
                failureDo(ctx, Result.failure(403, AuthUtil.MESSAGE_OF_PATH));
                return;
            }
        }

        //
        //权限验证
        //
        if (verifyPermissions != null && verifyPermissions.length > 0) {
            boolean isOk = false;
            if (verifyPermissionsAnd) {
                isOk = AuthUtil.verifyPermissionsAnd(verifyPermissions);
            } else {
                isOk = AuthUtil.verifyPermissions(verifyPermissions);
            }

            if (isOk == false) {
                //验证失败的
                failureDo(ctx, Result.failure(403, AuthUtil.MESSAGE_OF_PERMISSIONS));
                return;
            }
        }

        //
        //角色验证
        //
        if (verifyRoles != null && verifyRoles.length > 0) {
            boolean isOk = false;
            if (verifyRolesAnd) {
                isOk = AuthUtil.verifyRolesAnd(verifyRoles);
            } else {
                isOk = AuthUtil.verifyRoles(verifyRoles);
            }

            if (isOk == false) {
                //验证失败的
                failureDo(ctx, Result.failure(403, AuthUtil.MESSAGE_OF_ROLES));
                return;
            }
        }
    }

    private void failureDo(Context c, Result r) throws Throwable {
        c.setHandled(true);
        if (failureHandler == null) {
            failureHandler.onFailure(c, r);
        } else {
            AuthUtil.adapter().failure().onFailure(c, r);
        }
    }
}
