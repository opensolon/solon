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
package org.noear.solon.serialization.hessian;

import com.alibaba.com.caucho.hessian.io.ClassFactory;
import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import com.alibaba.com.caucho.hessian.io.SerializerFactory;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.lang.Nullable;
import org.noear.solon.serialization.EntityBytesSerializer;
import org.noear.solon.serialization.EntitySerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author noear
 * @since 2.8
 */
public class HessianBytesSerializer implements EntityBytesSerializer {
    private static final String label = "application/hessian";
    private static final HessianBytesSerializer _default = new HessianBytesSerializer();

    private static volatile SerializerFactory serializerFactory; // global serializer factory

    static {
        serializerFactory = initDefaultSerializerFactory();
    }

    /**
     * 默认实例
     */
    public static HessianBytesSerializer getDefault() {
        return _default;
    }

    /**
     * @deprecated 3.6 {@link #getDefault()}
     * */
    @Deprecated
    public static HessianBytesSerializer getInstance() {
        return _default;
    }

    /**
     * 内容类型
     */
    @Override
    public String mimeType() {
        return label;
    }

    /**
     * 数据类型
     * */
    @Override
    public Class<byte[]> dataType() {
        return byte[].class;
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
            return mime.startsWith(label);
        }
    }

    /**
     * 序列化器名字
     */
    @Override
    public String name() {
        return "hessian-bytes";
    }

    /**
     * 序列化
     *
     * @param obj 对象
     */
    @Override
    public byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Hessian2Output ho = new Hessian2Output(out);
        ho.writeObject(obj);
        ho.close();

        return out.toByteArray();
    }

    /**
     * 反序列化
     *
     * @param data   数据
     * @param toType 目标类型
     */
    @Override
    public Object deserialize(byte[] data, Type toType) throws IOException {
        Hessian2Input hi = new Hessian2Input(new ByteArrayInputStream(data));
        hi.setSerializerFactory(serializerFactory);
        return hi.readObject();
    }

    /**
     * 序列化主体
     *
     * @param ctx  请求上下文
     * @param data 数据
     */
    @Override
    public void serializeToBody(Context ctx, Object data) throws IOException {
        //如果没有设置过，用默认的 //如 ndjson,sse 或故意改变 mime（可由外部控制）
        if (ctx.contentTypeNew() == null) {
            ctx.contentType(this.mimeType());
        }

        Hessian2Output ho = new Hessian2Output(ctx.outputStream());
        if (data instanceof ModelAndView) {
            ho.writeObject(((ModelAndView) data).model());
        } else {
            ho.writeObject(data);
        }
        ho.flush();
    }

    /**
     * 反序列化主体
     *
     * @param ctx 请求上下文
     */
    @Override
    public Object deserializeFromBody(Context ctx, @Nullable Type bodyType) throws IOException {
        Hessian2Input hi = new Hessian2Input(ctx.bodyAsStream());
        hi.setSerializerFactory(serializerFactory);
        return hi.readObject();
    }

    private static SerializerFactory initDefaultSerializerFactory() {
        SerializerFactory defaultSerializerFactory = new SerializerFactory();
        ClassFactory classFactory = defaultSerializerFactory.getClassFactory();
        classFactory.setWhitelist(false); // blacklist mode
        classFactory.deny("org.apache.catalina.tribes.*");
        classFactory.deny("com.alibaba.citrus.springext.*");
        classFactory.deny("com.alipay.custrelation.*");
        classFactory.deny("com.alibaba.druid.*");
        return defaultSerializerFactory;
    }
}
