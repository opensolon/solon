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
package org.noear.solon.serialization.fastjson2;

import com.alibaba.fastjson2.JSONFactory;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import org.noear.solon.core.util.Assert;

/**
 *
 * @author noear
 * @since 3.6
 */
public class Fastjson2Decl<C,F> {
    private final boolean forSerialize;
    private C context;

    public Fastjson2Decl(C context) {
        this.context = context;

        if (context instanceof JSONWriter.Context) {
            forSerialize = true;
        } else {
            forSerialize = false;
        }
    }

    /**
     * 获取配置
     */
    public C getContext() {
        return context;
    }

    /**
     * 重置配置
     */
    public void setContext(C context) {
        Assert.notNull(context, "context can not be null");
        this.context = context;
    }


    /**
     * 设置特性
     */
    public void setFeatures(F... features) {
        if (forSerialize) {
            ((JSONWriter.Context) context).setFeatures(JSONFactory.getDefaultWriterFeatures());
        } else {
            ((JSONReader.Context) context).setFeatures(JSONFactory.getDefaultReaderFeatures());
        }

        addFeatures(features);
    }

    /**
     * 添加特性
     */
    public void addFeatures(F... features) {
        if (forSerialize) {
            //序列化
            for (F f1 : features) {
                JSONWriter.Feature feature = (JSONWriter.Feature) f1;
                ((JSONWriter.Context) context).config(feature, true);
            }
        } else {
            for (F f1 : features) {
                JSONReader.Feature feature = (JSONReader.Feature) f1;
                ((JSONReader.Context) context).config(feature, true);
            }
        }
    }

    /**
     * 移除特性
     */
    public void removeFeatures(F... features) {
        if (forSerialize) {
            //序列化
            for (F f1 : features) {
                JSONWriter.Feature feature = (JSONWriter.Feature) f1;
                ((JSONWriter.Context) context).config(feature, false);
            }
        } else {
            for (F f1 : features) {
                JSONReader.Feature feature = (JSONReader.Feature) f1;
                ((JSONReader.Context) context).config(feature, false);
            }
        }
    }
}