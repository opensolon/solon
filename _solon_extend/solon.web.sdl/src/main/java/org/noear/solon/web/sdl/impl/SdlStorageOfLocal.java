package org.noear.solon.web.sdl.impl;

import org.noear.solon.Utils;
import org.noear.solon.web.sdl.SdlStorage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 单设备登录数据服务 Local 实现
 *
 * @author noear
 * @since 2.2
 */
public class SdlStorageOfLocal implements SdlStorage {
    private Map<String, String> storage = new HashMap<>();

    /**
     * 更新单点登录标识
     */
    @Override
    public String updateUserSdlKey(Serializable userId) {
        String storageKey = SdlStorage.SESSION_SDL_KEY + ":" + userId;
        String userSdlKey = Utils.guid();

        //设置用户的单点登录标识（当前会话 与 用户的'单点登录标识'，说明当前会话不是最新的登录）
        storage.put(storageKey, userSdlKey);

        return userSdlKey;
    }

    /**
     * 移除单点登录标识
     * */
    @Override
    public void removeUserSdlKey(Serializable userId) {
        String storageKey = SdlStorage.SESSION_SDL_KEY + ":" + userId;

        storage.remove(storageKey);
    }

    /**
     * 获取单点登录标识
     */
    @Override
    public String getUserSdlKey(Serializable userId) {
        String storageKey = SdlStorage.SESSION_SDL_KEY + ":" + userId;

        return storage.get(storageKey);
    }
}
