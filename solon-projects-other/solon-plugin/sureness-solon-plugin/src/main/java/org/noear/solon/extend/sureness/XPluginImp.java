package org.noear.solon.extend.sureness;

import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.sureness.integration.SurenessConfiguration;
import com.usthe.sureness.subject.SubjectSum;
import com.usthe.sureness.util.JsonWebTokenUtil;
import com.usthe.sureness.util.SurenessContextHolder;
import org.noear.solon.core.Plugin;

import java.util.List;
import java.util.UUID;

/**
 * @author noear
 * @author tomsun28
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        context.beanScan(SurenessConfiguration.class);

        // issue jwt rest api
        Solon.app().get("/auth/token", ctx -> {
            SubjectSum subjectSum = SurenessContextHolder.getBindSubject();

            if (subjectSum == null) {
                ctx.render(Result.failure("Please auth!"));
            } else {
                String principal = (String) subjectSum.getPrincipal();
                List<String> roles = (List<String>) subjectSum.getRoles();
                // issue jwt
                String jwt = JsonWebTokenUtil.issueJwt(UUID.randomUUID().toString(), principal,
                        "token-server", 3600L, roles);
                ctx.render(Result.succeed(jwt));
            }
        });

        Solon.app().after("**", ctx -> SurenessContextHolder.unbindSubject());
    }
}
