/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.web.sdl;

import org.noear.solon.Solon;
import org.noear.solon.core.handle.Context;
import org.noear.solon.web.sdl.impl.SdlService;

import java.io.Serializable;

/**
 * 单设备工具类
 *
 * @author noear
 * @since 2.2
 */
public final class SdlUtil {
    static SdlService service = new SdlService();
    static SdlStorage storage;
    static boolean    enable = true;

    static {
        Solon.context().getBeanAsync(SdlStorage.class, bean -> {
            //从容器获取存储器
            storage = bean;
        });
    }

    /**
     * 手动设置存储器
     */
    public static void setStorage(SdlStorage storage) {
        SdlUtil.storage = storage;
    }


    /////////////////////////////////////////

    public static void enable(boolean enable){
        SdlUtil.enable = enable;
    }

    public static boolean enable(){
        return enable;
    }

    /**
     * 登录
     *
     * @param userId 用户Id
     */
    public static void login(Serializable userId) {
        login(Context.current(), userId);
    }

    /**
     * 登录
     *
     * @param userId 用户Id
     */
    public static void login(Context ctx, Serializable userId) {
        service.login(storage, ctx, userId);
    }

    /**
     * 登出（当前用户）
     */
    public static void logout() {
        //检测会话中的用户Id
        Serializable userId = service.getLoginedUserId(Context.current());

        if (userId != null) {
            logout(userId);
        }
    }

    /**
     * 登出（指定用户）
     */
    public static void logout(Serializable userId) {
        service.logout(storage, userId);
    }

    /**
     * 当前用户是否已登录
     */
    public static boolean isLogined() {
        return isLogined(Context.current());
    }

    /**
     * 当前用户是否已登录
     */
    public static boolean isLogined(Context ctx) {
        return service.isLogined(storage, ctx);
    }
}
