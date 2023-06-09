package org.noear.solon.web.sdl.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.web.sdl.SdlStorage;
import org.noear.solon.web.sdl.SdlUtil;

import java.io.Serializable;

/**
 * 单设备登录能力服务
 *
 * @author noear
 * @since 2.2
 */
public class SdlService {
    /**
     * 登录
     *
     * @param userId 用户Id
     */
    public void login(SdlStorage storage, Context ctx, Serializable userId) {
        String userSdlKey = storage.updateUserSdlKey(userId);

        ctx.sessionSet(SdlStorage.SESSION_USER_ID, userId);

        //设置当前会话的单点登录标识
        ctx.sessionSet(SdlStorage.SESSION_SDL_KEY, userSdlKey);
    }

    /**
     * 登出（指定用户）
     */
    public void logout(SdlStorage storage, Serializable userId) {
        storage.removeUserSdlKey(userId);
    }

    /**
     * 获取已登录用户Id
     */
    public Serializable getLoginedUserId(Context ctx) {
        return ctx.sessionOrDefault(SdlStorage.SESSION_USER_ID, null);
    }

    /**
     * 当前用户是否已登录
     */
    public boolean isLogined(SdlStorage storage, Context ctx) {
        //检测会话中的用户Id
        Serializable userId = ctx.sessionOrDefault(SdlStorage.SESSION_USER_ID, null);

        if (userId == null) {
            return false;
        }

        if(userId instanceof Number){
            //如果是数字，不能小于1
            if(((Number)userId).longValue() <1){
                return false;
            }
        } else {
            //如果是别的不能为空
            if (Utils.isEmpty(userId.toString())) {
                return false;
            }
        }

        //为单测增加支持（不然没法跑） //或者没有启用
        if (Solon.cfg().isDebugMode() || SdlUtil.enable() == false) {
            return true;
        }

        //检测会话中的用户SdlKey
        String userSdlKey = ctx.sessionOrDefault(SdlStorage.SESSION_SDL_KEY, "");

        if (Utils.isEmpty(userSdlKey)) {
            return false;
        }

        //检测用户SdlKey是否过时？
        String userSdlKeyOfUser = storage.getUserSdlKey(userId);
        return userSdlKey.equals(userSdlKeyOfUser);
    }
}
