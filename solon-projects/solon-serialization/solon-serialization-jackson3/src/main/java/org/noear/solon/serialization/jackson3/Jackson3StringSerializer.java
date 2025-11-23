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
package org.noear.solon.serialization.jackson3;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;

import org.noear.solon.Utils;
import org.noear.solon.core.convert.Converter;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.util.MimeType;
import org.noear.solon.lang.Nullable;
import org.noear.solon.serialization.EntityStringSerializer;
import org.noear.solon.serialization.jackson3.impl.NullValueSerializerImpl;
import org.noear.solon.serialization.jackson3.impl.TypeReferenceImpl;
import org.noear.solon.serialization.prop.JsonProps;
import org.noear.solon.serialization.prop.JsonPropsUtil2;

import com.fasterxml.jackson.annotation.JsonInclude;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.ser.jdk.JavaUtilDateSerializer;

/**
 * Jackson json 序列化
 *
 * @author noear
 * @since 1.5
 * @since 2.8
 */
public class Jackson3StringSerializer implements EntityStringSerializer {
    private static final String label = "/json";
    private static final Jackson3StringSerializer _default = new Jackson3StringSerializer();

    /**
     * 默认实例
     */
    public static Jackson3StringSerializer getDefault() {
        return _default;
    }


    private Jackson3Decl<SerializationFeature> serializeConfig;
    private Jackson3Decl<DeserializationFeature> deserializeConfig;

    public Jackson3StringSerializer(JsonProps jsonProps) {
        loadJsonProps(jsonProps);
    }

    public Jackson3StringSerializer() {

    }

    /**
     * 获取序列化配置
     *
     */
    public Jackson3Decl<SerializationFeature> getSerializeConfig() {
        if (serializeConfig == null) {
            serializeConfig = new Jackson3Decl<>();
        }

        return serializeConfig;
    }

    /**
     * 获取反序列化配置
     *
     */
    public Jackson3Decl<DeserializationFeature> getDeserializeConfig() {
        if (deserializeConfig == null) {
            deserializeConfig = new Jackson3Decl<>();
        }

        return deserializeConfig;
    }

    private AtomicBoolean initStatus = new AtomicBoolean(false);

    /**
     * 刷新
     */
    public void refresh() {
        initStatus.set(false);
    }

    /**
     * 初始化
     */
    protected void init() {
        if (initStatus.compareAndSet(false, true)) {
            getSerializeConfig().refresh();
            getDeserializeConfig().refresh();
        }
    }

    /**
     * 内容类型
     */
    @Override
    public String mimeType() {
        return "application/json";
    }

    /**
     * 数据类型
     *
     */
    @Override
    public Class<String> dataType() {
        return String.class;
    }

    /**
     * 是否匹配
     *
     * @param ctx  请求上下文
     * @param mime 内容类型
     */
    @Override
    public boolean matched(Context ctx, String mime) {
        if (mime == null) {
            return false;
        } else {
            return mime.contains(label) || mime.startsWith(MimeType.APPLICATION_X_NDJSON_VALUE);
        }
    }

    /**
     * 序列化器名字
     */
    @Override
    public String name() {
        return "jackson-json";
    }

    /**
     * 序列化
     *
     * @param obj 对象
     */
    @Override
    public String serialize(Object obj) throws IOException {
        init();
        return getSerializeConfig().getMapper().writeValueAsString(obj);
    }

    /**
     * 反序列化
     *
     * @param data   数据
     * @param toType 目标类型
     */
    @Override
    public Object deserialize(String data, Type toType) throws IOException {
        init();

        if (toType == null) {
            return getDeserializeConfig().getMapper().readTree(data);
        } else {
            if (toType instanceof Class) {
                //处理匿名名类
                Class<?> toClz = (Class<?>) toType;
                if (toClz.isAnonymousClass()) {
                    toType = toClz.getGenericSuperclass();
                }
            }

            TypeReferenceImpl typeRef = new TypeReferenceImpl(toType);
            return getDeserializeConfig().getMapper().readValue(data, typeRef);
        }
    }

    /**
     * 序列化主体
     *
     * @param ctx  请求上下文
     * @param data 数据
     */
    @Override
    public void serializeToBody(Context ctx, Object data) throws IOException {
        init();

        //如果没有设置过，用默认的 //如 ndjson,sse 或故意改变 mime（可由外部控制）
        if (ctx.contentTypeNew() == null) {
            ctx.contentType(this.mimeType());
        }

        if (data instanceof ModelAndView) {
            ctx.output(serialize(((ModelAndView) data).model()));
        } else {
            ctx.output(serialize(data));
        }
    }

    /**
     * 反序列化主体
     *
     * @param ctx 请求上下文
     */
    @Override
    public Object deserializeFromBody(Context ctx, @Nullable Type bodyType) throws IOException {
        init();

        String data = ctx.bodyNew();

        if (Utils.isNotEmpty(data)) {
            return getDeserializeConfig().getMapper().readTree(data);
        } else {
            return null;
        }
    }

    /**
     * 添加编码器
     *
     * @param clz     类型
     * @param encoder 编码器
     */
    public <T> void addEncoder(Class<T> clz, ValueSerializer<T> encoder) {
        getSerializeConfig().getCustomModule().addSerializer(clz, encoder);
    }

    /**
     * 添加转换器（编码器的简化版）
     *
     * @param clz       类型
     * @param converter 转换器
     */
    @Override
    public <T> void addEncoder(Class<T> clz, Converter<T, Object> converter) {
        if (clz == Date.class) {
            addEncoder(Date.class, new JavaUtilDateSerializer() {
            	@Override
            	public void serialize(Date date, JsonGenerator out, SerializationContext sp)
            			throws JacksonException {
                  if (this._customFormat == null) {
	                  writeDefaultValue(converter, clz.cast(date), out);
	              } else {
	                  super.serialize(date, out, sp);
	              }
            	}
            });
        } else {
            addEncoder(clz, new ValueSerializer<T>() {
            	@Override
            	public void serialize(T source, JsonGenerator out, SerializationContext sp) throws JacksonException {
            		writeDefaultValue(converter, source, out);
            	}
            });
        }
    }

    private static <T> void writeDefaultValue(Converter<T, Object> converter, T source, JsonGenerator out) throws JacksonException {
        Object val = converter.convert(source);

        if (val == null) {
            out.writeNull();
        } else if (val instanceof String) {
            out.writeString((String) val);
        } else if (val instanceof Number) {
            if (val instanceof Integer || val instanceof Long) {
                out.writeNumber(((Number) val).longValue());
            } else {
                out.writeNumber(((Number) val).doubleValue());
            }
        } else {
            throw new IllegalArgumentException("The result type of the converter is not supported: " + val.getClass().getName());
        }
    }

    protected void loadJsonProps(JsonProps jsonProps) {
        boolean writeNulls = false;
        ObjectMapper mapper = getSerializeConfig().getMapper();
        MapperBuilder builder = mapper.rebuild();
        if (jsonProps != null) {
            JsonPropsUtil2.dateAsFormat(this, jsonProps);
            JsonPropsUtil2.dateAsTicks(this, jsonProps);
            JsonPropsUtil2.boolAsInt(this, jsonProps);
            JsonPropsUtil2.longAsString(this, jsonProps);

            writeNulls = jsonProps.nullAsWriteable ||
                    jsonProps.nullNumberAsZero ||
                    jsonProps.nullArrayAsEmpty ||
                    jsonProps.nullBoolAsFalse ||
                    jsonProps.nullStringAsEmpty;

            
            if (writeNulls) {
            	builder.serializerFactory().withNullValueSerializer(new NullValueSerializerImpl(jsonProps));
            }

            if (jsonProps.enumAsName) {
            	builder.configure(EnumFeature.WRITE_ENUMS_USING_TO_STRING, true);
            }
        }

        if (writeNulls == false) {
        	JsonInclude.Value _val = JsonInclude.Value.construct(JsonInclude.Include.NON_NULL,null);
        	JsonInclude.Value.construct(JsonInclude.Include.NON_NULL,null)
//        	builder.changeDefaultPropertyInclusion();
            getSerializeConfig().getMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }

        //启用 transient 关键字
        getSerializeConfig().getMapper().configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
        //启用排序（即使用 LinkedHashMap）
        getSerializeConfig().getMapper().configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        //是否识别不带引号的key
        getSerializeConfig().getMapper().configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        //是否识别单引号的key
        getSerializeConfig().getMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        //浮点数默认类型（dubbod 转 BigDecimal）
        getSerializeConfig().getMapper().configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);


        //反序列化时候遇到不匹配的属性并不抛出异常
        getSerializeConfig().getMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //序列化时候遇到空对象不抛出异常
        getSerializeConfig().getMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //反序列化的时候如果是无效子类型,不抛出异常
        getSerializeConfig().getMapper().configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
    }
}