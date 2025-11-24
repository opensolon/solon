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
package org.noear.solon.serialization.jackson3.impl;

import java.util.Collection;
import java.util.List;

import org.noear.solon.serialization.prop.JsonProps;

import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.BeanDescription.Supplier;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.ValueSerializerModifier;

/**
 * @author noear
 * @since 1.12
 */
public class NullBeanSerializerModifierImpl extends ValueSerializerModifier {
    private JsonProps jsonProps;
    public NullBeanSerializerModifierImpl(JsonProps jsonProps){
        this.jsonProps = jsonProps;
    }
    
    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, Supplier beanDesc,
    		List<BeanPropertyWriter> beanProperties) {
    	//循环所有的beanPropertyWriter
        for (Object beanProperty : beanProperties) {
            BeanPropertyWriter writer = (BeanPropertyWriter) beanProperty;

            if (isArrayType(writer)) {
                writer.assignNullSerializer(new NullValueSerializerImpl(jsonProps,  writer.getType()));
            } else if (isNumberType(writer)) {
                writer.assignNullSerializer(new NullValueSerializerImpl(jsonProps,  writer.getType()));
            } else if (isBooleanType(writer)) {
                writer.assignNullSerializer(new NullValueSerializerImpl(jsonProps,  writer.getType()));
            } else if (isStringType(writer)) {
                writer.assignNullSerializer(new NullValueSerializerImpl(jsonProps,  writer.getType()));
            }
        }
    	return super.changeProperties(config, beanDesc, beanProperties);
    }

    /**
     * 是否是数组
     */
    private boolean isArrayType(BeanPropertyWriter writer) {
        Class<?> clazz = writer.getType().getRawClass();
        return clazz.isArray() || Collection.class.isAssignableFrom(clazz);
    }

    /**
     * 是否是string
     */
    private boolean isStringType(BeanPropertyWriter writer) {
        Class<?> clazz = writer.getType().getRawClass();
        return CharSequence.class.isAssignableFrom(clazz) || Character.class.isAssignableFrom(clazz);
    }


    /**
     * 是否是int
     */
    private boolean isNumberType(BeanPropertyWriter writer) {
        Class<?> clazz = writer.getType().getRawClass();
        return Number.class.isAssignableFrom(clazz);
    }

    /**
     * 是否是boolean
     */
    private boolean isBooleanType(BeanPropertyWriter writer) {
        Class<?> clazz = writer.getType().getRawClass();
        return clazz.equals(Boolean.class);
    }

}
