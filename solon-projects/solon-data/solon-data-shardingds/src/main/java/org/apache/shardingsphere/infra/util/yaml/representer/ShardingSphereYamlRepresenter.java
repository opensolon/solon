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
package org.apache.shardingsphere.infra.util.yaml.representer;

import org.apache.shardingsphere.infra.util.spi.ShardingSphereServiceLoader;
import org.apache.shardingsphere.infra.util.yaml.representer.processor.DefaultYamlTupleProcessor;
import org.apache.shardingsphere.infra.util.yaml.representer.processor.ShardingSphereYamlTupleProcessor;
import org.apache.shardingsphere.infra.util.yaml.shortcuts.ShardingSphereYamlShortcuts;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * ShardingSphere YAML representer（由于Yaml版本不同，需要重写ShardingSphereYamlRepresenter的构造方法，否则会报错）
 *
 * @author Sorghum
 * @since 2.3
 */
public final class ShardingSphereYamlRepresenter extends Representer {
    
    public ShardingSphereYamlRepresenter() {
        super(new DumperOptions());
        Map<String, Class<?>> yamlShortcuts = new HashMap<>();
        ShardingSphereServiceLoader.getServiceInstances(ShardingSphereYamlShortcuts.class).stream().map(ShardingSphereYamlShortcuts::getYamlShortcuts).forEach(yamlShortcuts::putAll);
        yamlShortcuts.forEach((key, value) -> addClassTag(value, new Tag(key)));
    }
    
    @Override
    protected NodeTuple representJavaBeanProperty(final Object javaBean, final Property property, final Object propertyValue, final Tag customTag) {
        NodeTuple nodeTuple = super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
        for (ShardingSphereYamlTupleProcessor each : ShardingSphereServiceLoader.getServiceInstances(ShardingSphereYamlTupleProcessor.class)) {
            if (property.getName().equals(each.getTupleName())) {
                return each.process(nodeTuple);
            }
        }
        return new DefaultYamlTupleProcessor().process(nodeTuple);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected Node representMapping(final Tag tag, final Map<?, ?> mapping, final DumperOptions.FlowStyle flowStyle) {
        Map skippedEmptyValuesMapping = new LinkedHashMap<>(mapping.size(), 1);
        for (Entry<?, ?> entry : mapping.entrySet()) {
            if (entry.getValue() instanceof Collection && ((Collection) entry.getValue()).isEmpty()) {
                continue;
            }
            if (entry.getValue() instanceof Map && ((Map) entry.getValue()).isEmpty()) {
                continue;
            }
            skippedEmptyValuesMapping.put(entry.getKey(), entry.getValue());
        }
        return super.representMapping(tag, skippedEmptyValuesMapping, flowStyle);
    }
}
