package demo.solution;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;

import java.util.Base64;

/**
 * Authorization: Basic MTIzOjEyMw==
 *
 * @author noear
 * @since 1.4
 */
@Deprecated
public class BasicAuth {
    private static final String TYPE = "Basic ";

    public void auth(Context ctx) {
        String auth0 = ctx.header("Authorization");

        if (Utils.isEmpty(auth0) || auth0.startsWith(TYPE) == false) {
            ctx.status(401);
            ctx.headerSet("WWW-Authenticate", "Basic realm...");
            return;
        }

        String auth1 = auth0.substring(TYPE.length()).trim();
        String auth2 = new String(Base64.getDecoder().decode(auth1));

        String username = auth2.split(":")[0];
        String paasword = auth2.split(":")[0];

    }
}
