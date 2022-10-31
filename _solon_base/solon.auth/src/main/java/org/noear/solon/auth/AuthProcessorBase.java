package org.noear.solon.auth;

import org.noear.solon.auth.annotation.Logical;

import java.util.List;

/**
 * 认证处理器加强基类
 *
 * @author noear
 * @since 1.4
 */
public abstract class AuthProcessorBase implements AuthProcessor {

    /**
     * 验证IP
     */
    @Override
    public boolean verifyIp(String ip) {
        return false;
    }

    /**
     * 验证登录状态
     */
    @Override
    public boolean verifyLogined() {
        return false;
    }

    /**
     * 验证路径（一般使用路径验证）
     *
     * @param path   路径
     * @param method 请求方式
     */
    @Override
    public boolean verifyPath(String path, String method) {
        return false;
    }

    /**
     * 验证特定权限（有特殊情况用权限验证）
     *
     * @param permissions 权限
     * @param logical     认证的逻辑关系
     */
    @Override
    public boolean verifyPermissions(String[] permissions, Logical logical) {
        List<String> list = getPermissions();

        if (list == null || list.size() == 0) {
            return false;
        }

        if (Logical.AND == logical) {
            boolean isOk = true;

            for (String v : permissions) {
                isOk = isOk && list.contains(v);
            }

            return isOk;
        } else {
            for (String v : permissions) {
                if (list.contains(v)) {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * 验证特定角色（有特殊情况用角色验证）
     *
     * @param roles   角色
     * @param logical 认证的逻辑关系
     */
    @Override
    public boolean verifyRoles(String[] roles, Logical logical) {
        List<String> list = getRoles();

        if (list == null || list.size() == 0) {
            return false;
        }

        if (Logical.AND == logical) {
            boolean isOk = true;

            for (String v : roles) {
                isOk = isOk && list.contains(v);
            }

            return isOk;
        } else {
            for (String v : roles) {
                if (list.contains(v)) {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * 获取用户权限列表
     */
    protected abstract List<String> getPermissions();

    /**
     * 获取用户角色列表
     */
    protected abstract List<String> getRoles();
}
