package org.noear.solon.auth;

import org.noear.solon.auth.annotation.Logical;

import java.util.List;

/**
 * @author noear
 * @since 1.4
 */
public abstract class AuthProcessorBase implements AuthProcessor {
    @Override
    public boolean verifyIp(String ip) {
        return false;
    }

    @Override
    public boolean verifyLogined() {
        return false;
    }

    @Override
    public boolean verifyPath(String path, String method) {
        return false;
    }

    @Override
    public boolean verifyPermissions(String[] permissions, Logical logical) {
        List<String> list = getPermissions();

        if (list.size() == 0) {
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

    @Override
    public boolean verifyRoles(String[] roles, Logical logical) {
        List<String> list = getRoles();

        if (list.size() == 0) {
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

    abstract List<String> getPermissions();

    abstract List<String> getRoles();
}
