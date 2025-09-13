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
package org.noear.solon.serialization.snack3;

import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.solon.core.util.Assert;

/**
 * Json 声明
 *
 * @author noear
 * @since 3.5
 */
public class SnackDecl {
    private Options options;

    public SnackDecl() {
        this.options = Options.def();
    }

    /**
     * 获取选项
     */
    public Options getOptions() {
        return options;
    }

    /**
     * 设置选项
     */
    public void setOptions(Options options) {
        Assert.notNull(options, "options can't be null");
        this.options = options;
    }

    /**
     * 设置特性（即重置特性）
     */
    public void setFeatures(Feature... features) {
        getOptions().setFeatures(features);
    }

    /**
     * 添加特性
     */
    public void addFeatures(Feature... features) {
        getOptions().add(features);
    }

    /**
     * 移除特性
     */
    public void removeFeatures(Feature... features) {
        getOptions().remove(features);
    }
}