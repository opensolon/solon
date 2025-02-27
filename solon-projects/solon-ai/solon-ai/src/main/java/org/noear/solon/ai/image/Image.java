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

import java.util.Base64;

/**
 * 图像
 *
 * @author noear
 * @since 3.1
 */
public class Image {
    private String b64_json; //就是 base64-str
    private String url;

    public Image() {
        //...用于反序列化
    }

    public static Image ofUrl(String url) {
        Image img = new Image();
        img.url = url;
        return img;
    }

    public static Image ofBase64(String base64) {
        Image img = new Image();
        img.b64_json = base64;
        return img;
    }

    public static Image ofBase64(byte[] base64) {
        Image img = new Image();
        img.b64_json = Base64.getEncoder().encodeToString(base64);
        return img;
    }

    public String getB64Json() {
        return b64_json;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "Image{" +
                "b64_json='" + b64_json + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}