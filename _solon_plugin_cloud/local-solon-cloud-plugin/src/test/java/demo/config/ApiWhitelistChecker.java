package demo.config;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Component;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.core.handle.Context;
import org.noear.solon.validation.annotation.Whitelist;
import org.noear.solon.validation.annotation.WhitelistChecker;

/**
 * 提供 Whitelist 的检查能力
 */
@Component
public class ApiWhitelistChecker implements WhitelistChecker {
    @Override
    public boolean check(Whitelist anno, Context ctx) {
        String listName = anno.value();
        if (Utils.isEmpty(listName)) {
            listName = "whitelist";
        }

        return CloudClient.list().inListOfIp(listName, ctx.realIp());
    }
}
