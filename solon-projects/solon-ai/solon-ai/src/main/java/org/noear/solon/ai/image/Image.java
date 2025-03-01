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
package org.noear.solon.ai.image;

import org.noear.solon.Utils;
import org.noear.solon.ai.AiMedia;

import java.util.Base64;

/**
 * 图像
 *
 * @author noear
 * @since 3.1
 */
public class Image implements AiMedia {
    private String b64_json; //就是 base64-str
    private String url;

    public Image() {
        //...用于反序列化
    }

    /**
     * 由 url 构建
     */
    public static Image ofUrl(String url) {
        Image tmp = new Image();
        tmp.url = url;
        return tmp;
    }

    /**
     * 由 base64String 构建
     */
    public static Image ofBase64(String base64String) {
        Image tmp = new Image();
        tmp.b64_json = base64String;
        return tmp;
    }

    /**
     * 由 base64 构建
     */
    public static Image ofBase64(byte[] base64) {
        Image tmp = new Image();
        tmp.b64_json = Base64.getEncoder().encodeToString(base64);
        return tmp;
    }

    /**
     * 获取 base64
     */
    public String getB64Json() {
        return b64_json;
    }

    /**
     * 获取 url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 转为带媒体信息的字符串
     */
    @Override
    public String toDataString(boolean useMime) {
        if (Utils.isEmpty(b64_json)) {
            return url;
        } else {
            if (useMime) {
                return "data:image/jpeg;base64," + b64_json;
            } else {
                return b64_json;
            }
        }
    }

    @Override
    public String toString() {
        return "Image{" +
                "b64_json='" + b64_json + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}