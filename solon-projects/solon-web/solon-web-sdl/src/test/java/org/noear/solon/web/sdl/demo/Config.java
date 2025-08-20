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
package org.noear.solon.web.sdl.demo;

import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.validation.annotation.LoginedChecker;
import org.noear.solon.web.sdl.SdlLoginedChecker;
import org.noear.solon.web.sdl.SdlStorage;
import org.noear.solon.web.sdl.impl.SdlStorageOfLocal;

/**
 * 主要是为了构建： SdlStorage 和 SdlLoginedChecker
 *
 * @author noear 2023/4/5 created
 */
@Configuration
public class Config {
    // 用本地内存存储，一般用于临时测试
    @Managed
    public SdlStorage sdlStorage1() {
        return new SdlStorageOfLocal();
    }

    @Managed
    public LoginedChecker sdlLoginedChecker() {
        return new SdlLoginedChecker();
    }
}
