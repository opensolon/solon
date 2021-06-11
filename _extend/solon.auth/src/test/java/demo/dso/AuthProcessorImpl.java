package demo.dso;

import org.noear.solon.auth.AuthProcessor;
import org.noear.solon.auth.annotation.Logical;

/**
 * @author noear
 */
public class AuthProcessorImpl implements AuthProcessor {
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
        return false;
    }

    @Override
    public boolean verifyRoles(String[] roles, Logical logical) {
        return false;
    }
}
