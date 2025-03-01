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
package org.noear.solon.ai.audio;


import org.noear.solon.ai.AiMedia;

/**
 * 音频
 *
 * @author noear
 * @since 3.1
 */
public class Audio implements AiMedia {
    private String url;

    /**
     * 由 url 构建
     */
    public static Audio ofUrl(String url) {
        Audio tmp = new Audio();
        tmp.url = url;
        return tmp;
    }

    /**
     * 获取 url
     */
    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "Audio{" +
                "url='" + url + '\'' +
                '}';
    }
}
