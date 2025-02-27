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
    private String b64_json;
    private String url;

    public Image() {
        //...用于反序列化
    }

    public Image(String data) {
        if (data.contains("://")) {
            this.url = data;
        } else {
            this.b64_json = data;
        }
    }

    public Image(byte[] data) {
        this.b64_json = Base64.getEncoder().encodeToString(data);
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