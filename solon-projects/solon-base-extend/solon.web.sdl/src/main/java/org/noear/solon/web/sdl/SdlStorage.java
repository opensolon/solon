package org.noear.solon.web.sdl;

import java.io.Serializable;

/**
 * 单点登录存储服务
 *
 * @author noear
 * @since 2.2
 */
public interface SdlStorage {
    String SESSION_USER_ID = "user_id";
    String SESSION_SDL_KEY = "user_sdl_key";

    /**
     * 更新单点登录标识
     */
    String updateUserSdlKey(Serializable userId);

    /**
     * 移除单点登录标识
     * */
    void removeUserSdlKey(Serializable userId);

    /**
     * 获取单点登录标识
     */
    String getUserSdlKey(Serializable userId);
}
