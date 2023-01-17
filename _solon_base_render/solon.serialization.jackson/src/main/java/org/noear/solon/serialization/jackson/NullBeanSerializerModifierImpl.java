package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import org.noear.solon.serialization.prop.JsonProps;

import java.util.Collection;
import java.util.List;

/**
 * @author noear
 * @since 1.12
 */
public class NullBeanSerializerModifierImpl extends BeanSerializerModifier {
    private JsonProps jsonProps;
    public NullBeanSerializerModifierImpl(JsonProps jsonProps){
        this.jsonProps = jsonProps;
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        //循环所有的beanPropertyWriter
        for (Object beanProperty : beanProperties) {
            BeanPropertyWriter writer = (BeanPropertyWriter) beanProperty;

            if (isArrayType(writer)) {
                writer.assignNullSerializer(new NullValueSerializer(jsonProps,  writer.getType()));
            } else if (isNumberType(writer)) {
                writer.assignNullSerializer(new NullValueSerializer(jsonProps,  writer.getType()));
            } else if (isBooleanType(writer)) {
                writer.assignNullSerializer(new NullValueSerializer(jsonProps,  writer.getType()));
            } else if (isStringType(writer)) {
                writer.assignNullSerializer(new NullValueSerializer(jsonProps,  writer.getType()));
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
