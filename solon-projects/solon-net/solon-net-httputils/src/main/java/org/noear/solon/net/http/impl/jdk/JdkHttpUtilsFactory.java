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
package org.noear.solon.net.http.impl.jdk;

import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.HttpUtilsFactory;

/**
 * Http 工具工厂 JDK HttpURLConnection 实现
 *
 * @author noear
 * @since 3.0
 */
public class JdkHttpUtilsFactory implements HttpUtilsFactory {
    private static final JdkHttpUtilsFactory instance = new JdkHttpUtilsFactory();

    public static JdkHttpUtilsFactory getInstance() {
        return instance;
    }

    @Override
    public HttpUtils http(String url) {
        return new JdkHttpUtils(url);
    }
}
