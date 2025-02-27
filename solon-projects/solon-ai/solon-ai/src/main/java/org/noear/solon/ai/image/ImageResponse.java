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
import org.noear.solon.ai.AiUsage;

import java.util.List;

/**
 * 图像响应
 *
 * @author noear
 * @since 3.1
 */
public class ImageResponse {
    private final String model;
    private final ImageException error;
    private final List<Image> data;
    private final AiUsage usage;

    public ImageResponse(String model, ImageException error, List<Image> data, AiUsage usage) {
        this.model = model;
        this.error = error;
        this.data = data;
        this.usage = usage;
    }

    /**
     * 获取模型
     */
    public String getModel() {
        return model;
    }

    /**
     * 获取异常
     */
    public ImageException getError() {
        return error;
    }

    public boolean hasData() {
        return Utils.isNotEmpty(data);
    }

    /**
     * 获取数据
     */
    public List<Image> getData() {
        return data;
    }

    /**
     * 获取图片
     */
    public Image getImage() {
        return data.get(0);
    }

    /**
     * 获取使用情况
     */
    public AiUsage getUsage() {
        return usage;
    }

    @Override
    public String toString() {
        return "{" +
                "model='" + model + '\'' +
                ", data=" + data +
                ", usage=" + usage +
                '}';
    }
}