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

import java.util.HashSet;
import java.util.Set;

/**
 * @author noear
 * @since 3.6
 */
public class JacksonDecl<F extends ConfigFeature> {
    private ObjectMapper mapper;
    private Set<F> features;
    private SimpleModule customModule;

    public JacksonDecl() {
        this.mapper = new ObjectMapper();
        this.features = new HashSet<>();
        this.customModule = new SimpleModule();
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setMapper(ObjectMapper mapper) {
        Assert.notNull(mapper, "This mapper is null");
        this.mapper = mapper;
    }

    public Set<F> getFeatures() {
        return features;
    }

    public SimpleModule getCustomModule() {
        return customModule;
    }

    protected void refresh() {
        for (ConfigFeature f1 : features) {
            if (f1 instanceof SerializationFeature) {
                mapper.enable((SerializationFeature) f1);
            } else if (f1 instanceof DeserializationFeature) {
                mapper.enable((DeserializationFeature) f1);
            }
        }

        mapper.registerModule(customModule);
    }
}
