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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.noear.solon.core.util.LazyReference;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.serialization.AbstractStringEntityConverter;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.jackson3.impl.TimeDeserializer;
import org.noear.solon.serialization.jackson3.impl.TypeReferenceImpl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;

import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Jackson 实体转换器
 *
 * @author noear
 * @since 3.6
 */
public class Jackson3EntityConverter extends AbstractStringEntityConverter<Jackson3StringSerializer> {
    public Jackson3EntityConverter(Jackson3StringSerializer serializer) {
        super(serializer);

        serializer.getDeserializeConfig().setMapper(newMapper(new JavaTimeModule()));

        serializer.getDeserializeConfig().getCustomModule().addDeserializer(LocalDateTime.class, new TimeDeserializer<>(LocalDateTime.class));
        serializer.getDeserializeConfig().getCustomModule().addDeserializer(LocalDate.class, new TimeDeserializer<>(LocalDate.class));
        serializer.getDeserializeConfig().getCustomModule().addDeserializer(LocalTime.class, new TimeDeserializer<>(LocalTime.class));
        serializer.getDeserializeConfig().getCustomModule().addDeserializer(Date.class, new TimeDeserializer<>(Date.class));

        ObjectMapper mapper = serializer.getSerializeConfig().getMapper();
        mapper.serializationConfig().with(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registeredModules().add(new JavaTimeModule());
        serializer.getSerializeConfig().setMapper(mapper);
    }

    /**
     * 后缀或名字映射
     */
    @Override
    public String[] mappings() {
        return new String[]{SerializerNames.AT_JSON};
    }
    
    /**
     * 初始化
     *
     * @param modules 配置模块
     */
    public ObjectMapper newMapper(tools.jackson.databind.JacksonModule... modules) {
        ObjectMapper mapper = new ObjectMapper();
     
        ObjectMapper customMapper = mapper.rebuild()
        		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        		.changeDefaultVisibility(vc -> vc.withVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY))
        		.activateDefaultTypingAsProperty(mapper._deserializationContext().getConfig().getPolymorphicTypeValidator(),
        				DefaultTyping.JAVA_LANG_OBJECT, "@type")
        		.addModules(modules).build();// 注册 JavaTimeModule ，以适配 java.time 下的时间类型
        // 允许使用未带引号的字段名
        customMapper.serializationConfig().with(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES);
        // 允许使用单引号
        customMapper.serializationConfig().with(JsonReadFeature.ALLOW_SINGLE_QUOTES);	
        return customMapper;
    }


    /**
     * 转换 body
     *
     * @param ctx   请求上下文
     * @param mWrap 函数包装器
     */
    @Override
    protected Object changeBody(org.noear.solon.core.handle.Context ctx, MethodWrap mWrap) throws Exception {
    	 return serializer.deserializeFromBody(ctx);
    }

    /**
     * 转换 value
     *
     * @param ctx     请求上下文
     * @param p       参数包装器
     * @param pi      参数序位
     * @param pt      参数类型
     * @param bodyRef 主体对象
     * @since 1.11 增加 requireBody 支持
     */
    @Override
    protected Object changeValue(org.noear.solon.core.handle.Context ctx, ParamWrap p, int pi, Class<?> pt, LazyReference bodyRef) throws Throwable {
        if (p.spec().isRequiredPath() || p.spec().isRequiredCookie() || p.spec().isRequiredHeader()) {
            //如果是 path、cookie, header
            return super.changeValue(ctx, p, pi, pt, bodyRef);
        }

        if (p.spec().isRequiredBody() == false && ctx.paramMap().containsKey(p.spec().getName())) {
            //有可能是path、queryString变量
            return super.changeValue(ctx, p, pi, pt, bodyRef);
        }

        Object bodyObj = bodyRef.get();

        if (bodyObj == null) {
            return super.changeValue(ctx, p, pi, pt, bodyRef);
        }

        JsonNode tmp = (JsonNode) bodyObj;

        if (tmp.isObject()) {
            if (p.spec().isRequiredBody() == false) {
                //
                //如果没有 body 要求；尝试找按属性找
                //
                if (tmp.has(p.spec().getName())) {
                    JsonNode m1 = tmp.get(p.spec().getName());

                    return serializer.getDeserializeConfig().getMapper().readValue(serializer.getDeserializeConfig().getMapper().treeAsTokens(m1), new TypeReferenceImpl<>(p));
                }
            }

            //尝试 body 转换
            if (pt.isPrimitive() || pt.getTypeName().startsWith("java.lang.")) {
                return super.changeValue(ctx, p, pi, pt, bodyRef);
            } else {
                if (List.class.isAssignableFrom(pt)) {
                    return null;
                }

                if (pt.isArray()) {
                    return null;
                }

                //支持泛型的转换 如：Map<T>
                return serializer.getDeserializeConfig().getMapper().readValue(serializer.getDeserializeConfig().getMapper().treeAsTokens(tmp), new TypeReferenceImpl<>(p));
            }
        }

        if (tmp.isArray()) {
            //如果参数是非集合类型
            if (!Collection.class.isAssignableFrom(pt)) {
                return null;
            }

            return serializer.getDeserializeConfig().getMapper().readValue(serializer.getDeserializeConfig().getMapper().treeAsTokens(tmp), new TypeReferenceImpl<>(p));
        }

        //return tmp.val().getRaw();
        if (tmp.isValueNode()) {
            return serializer.getDeserializeConfig().getMapper().readValue(serializer.getDeserializeConfig().getMapper().treeAsTokens(tmp), new TypeReferenceImpl<>(p));
        } else {
            return null;
        }
    }
}
