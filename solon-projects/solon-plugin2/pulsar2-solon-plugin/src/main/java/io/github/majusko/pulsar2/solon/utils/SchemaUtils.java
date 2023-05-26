package io.github.majusko.pulsar2.solon.utils;

import com.google.protobuf.GeneratedMessageV3;

import io.github.majusko.pulsar2.solon.constant.Serialization;
import io.github.majusko.pulsar2.solon.error.exception.ProducerInitException;

import org.apache.pulsar.client.api.Schema;

import java.lang.reflect.Method;

public class SchemaUtils {

    private SchemaUtils() {
    }

    private static <T> Schema<?> getGenericSchema(Serialization serialization, Class<T> clazz) throws RuntimeException {
        switch (serialization) {
            case JSON: {
                return Schema.JSON(clazz);
            }
            case AVRO: {
                return Schema.AVRO(clazz);
            }
            case STRING: {
                return Schema.STRING;
            }
            default: {
                throw new ProducerInitException("Unknown producer schema.");
            }
        }
    }

    private static <T extends GeneratedMessageV3> Schema<?> getProtoSchema(Serialization serialization, Class<T> clazz) throws RuntimeException {
        if (serialization == Serialization.PROTOBUF) {
            return Schema.PROTOBUF(clazz);
        }
        throw new ProducerInitException("Unknown producer schema.");
    }

    public static Schema<?> getSchema(Serialization serialisation, Class<?> clazz) {
        if (clazz == byte[].class) {
            return Schema.BYTES;
        }

        if (isProto(serialisation)) {
            return getProtoSchema(serialisation, (Class<? extends GeneratedMessageV3>) clazz);
        }

        return getGenericSchema(serialisation, clazz);
    }

    public static boolean isProto(Serialization serialization) {
        return serialization == Serialization.PROTOBUF;
    }

    public static Class<?> getParameterType(Method method) {
        return method.getParameterTypes()[0];
    }

}
