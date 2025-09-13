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
package org.noear.solon.serialization.fury;

import org.apache.fury.Fury;
import org.apache.fury.ThreadLocalFury;
import org.apache.fury.ThreadSafeFury;
import org.apache.fury.config.Language;
import org.apache.fury.resolver.AllowListChecker;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.lang.Nullable;
import org.noear.solon.serialization.ContextSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Fury 字节序列化
 *
 * @author noear
 * @since 2.8
 */
public class FuryBytesSerializer implements ContextSerializer<byte[]> {
    private static final String label = "application/fury";
    private static final FuryBytesSerializer _default = new FuryBytesSerializer();

    public static FuryBytesSerializer getDefault() {
        return _default;
    }

    /**
     * @deprecated 3.6 {@link #getDefault()}
     * */
    @Deprecated
    public static FuryBytesSerializer getInstance() {
        return _default;
    }


    private final Collection<String> blackList;
    private final AllowListChecker blackListChecker;
    private final ThreadSafeFury fury;

    public FuryBytesSerializer() {
        blackList = BlackListUtil.getBlackList();
        blackListChecker = new AllowListChecker(AllowListChecker.CheckLevel.WARN);

        fury = new ThreadLocalFury(classLoader -> {
            Fury tmp = Fury.builder()
                    .withAsyncCompilation(true)
                    .withLanguage(Language.JAVA)
                    .withRefTracking(true)
                    .requireClassRegistration(false)
                    .build();

            tmp.getClassResolver().setClassChecker(blackListChecker);
            blackListChecker.addListener(tmp.getClassResolver());
            for (String key : blackList) {
                blackListChecker.disallowClass(key + "*");
            }
            return tmp;
        });
    }

    /**
     * 添加默名单
     */
    public void addBlacklist(String classNameOrPrefix) {
        blackListChecker.disallowClass(classNameOrPrefix);
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
        return "fury-bytes";
    }

    /**
     * 序列化
     *
     * @param obj 对象
     */
    @Override
    public byte[] serialize(Object obj) throws IOException {
        return fury.serialize(obj);
    }

    /**
     * 反序列化
     *
     * @param data   数据
     * @param toType 目标类型
     */
    @Override
    public Object deserialize(byte[] data, Type toType) throws IOException {
        if (toType == null) {
            return fury.deserialize(data);
        } else {
            if (toType instanceof Class) {
                //处理匿名名类
                Class<?> toClz = (Class<?>) toType;
                if (toClz.isAnonymousClass()) {
                    toType = toClz.getGenericSuperclass();
                }
            }

            Class<?> clz = ClassUtil.getTypeClass(toType);
            return fury.deserializeJavaObject(data, clz);
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
        return fury.deserialize(ctx.bodyAsBytes());
    }
}