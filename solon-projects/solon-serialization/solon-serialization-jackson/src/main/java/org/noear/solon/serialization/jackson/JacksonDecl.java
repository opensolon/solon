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
package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.ConfigFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.noear.solon.core.util.Assert;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author noear
 * @since 3.6
 */
public class JacksonDecl<F extends ConfigFeature> {
    private ObjectMapper mapper;
    private Set<F> featuresSet;
    private SimpleModule customModule;

    public JacksonDecl() {
        this.mapper = new ObjectMapper();
        this.featuresSet = new HashSet<>();
        this.customModule = new SimpleModule();
    }

    /**
     * 获取映射器
     */
    public ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * 设置映射器
     */
    public void setMapper(ObjectMapper mapper) {
        Assert.notNull(mapper, "mapper can't be null");
        this.mapper = mapper;
    }

    /**
     * 获取定制模块
     */
    public SimpleModule getCustomModule() {
        return customModule;
    }

    /**
     * 设置特性（即重置特性）
     */
    public void setFeatures(F... features) {
        this.featuresSet.clear();
        this.featuresSet.addAll(Arrays.asList(features));
    }

    /**
     * 添加特性
     */
    public void addFeatures(F... features) {
        featuresSet.addAll(Arrays.asList(features));
    }

    /**
     * 移除特性
     */
    public void removeFeatures(F... features) {
        featuresSet.removeAll(Arrays.asList(features));
    }

    protected void refresh() {
        for (ConfigFeature f1 : featuresSet) {
            if (f1 instanceof SerializationFeature) {
                mapper.enable((SerializationFeature) f1);
            } else if (f1 instanceof DeserializationFeature) {
                mapper.enable((DeserializationFeature) f1);
            }
        }

        if (customModule != null) {
            mapper.registerModule(customModule);
        }
    }
}