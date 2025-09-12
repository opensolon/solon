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
package org.noear.solon.serialization.jackson.xml;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.ConfigFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.util.HashSet;
import java.util.Set;

/**
 * @author noear
 * @since 3.6
 */
public class JacksonXmlDeclaration <F extends ConfigFeature> {
    private XmlMapper mapper;
    private Set<F> features;
    private SimpleModule customModule;

    public JacksonXmlDeclaration() {
        this.mapper = new XmlMapper();
        this.features = new HashSet<>();
        this.customModule = new SimpleModule();
    }

    public XmlMapper getMapper() {
        return mapper;
    }

    public void setMapper(XmlMapper config) {
        this.mapper = config;
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