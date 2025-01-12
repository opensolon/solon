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
