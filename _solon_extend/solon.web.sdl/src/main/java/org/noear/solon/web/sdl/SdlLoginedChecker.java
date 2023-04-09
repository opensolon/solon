package org.noear.solon.web.sdl;

import org.noear.solon.core.handle.Context;
import org.noear.solon.validation.annotation.Logined;
import org.noear.solon.validation.annotation.LoginedChecker;

/**
 * 单设备登录检测器
 *
 * @author noear
 * @since 2.2
 */
public class SdlLoginedChecker implements LoginedChecker {
    @Override
    public boolean check(Logined anno, Context ctx, String userKeyName) {
        return SdlUtil.isLogined(ctx);
    }
}
