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
package org.noear.solon.serialization.fory;

import org.apache.fory.Fory;
import org.apache.fory.ThreadLocalFory;
import org.apache.fory.ThreadSafeFory;
import org.apache.fory.config.Language;
import org.apache.fory.resolver.AllowListChecker;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.lang.Nullable;
import org.noear.solon.serialization.EntityBytesSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Fory 字节序列化
 *
 * @author noear
 * @since 2.8
 */
public class ForyBytesSerializer implements EntityBytesSerializer {
    private static final String label = "application/fory";
    private static final ForyBytesSerializer _default = new ForyBytesSerializer();

    /**
     * 默认实例
     */
    public static ForyBytesSerializer getDefault() {
        return _default;
    }


    private final Collection<String> blackList;
    private final AllowListChecker blackListChecker;
    private final ThreadSafeFory fory;

    public ForyBytesSerializer() {
        blackList = BlackListUtil.getBlackList();
        blackListChecker = new AllowListChecker(AllowListChecker.CheckLevel.WARN);

        fory = new ThreadLocalFory(classLoader -> {
            Fory tmp = Fory.builder()
                    .withAsyncCompilation(true)
                    .withLanguage(Language.JAVA)
                    .withRefTracking(true)
                    .requireClassRegistration(false)
                    .build();

            for (String key : blackList) {
                blackListChecker.disallowClass(key + "*");
            }
            tmp.getTypeResolver().setTypeChecker(blackListChecker);

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
        return "fory-bytes";
    }

    /**
     * 序列化
     *
     * @param obj 对象
     */
    @Override
    public byte[] serialize(Object obj) throws IOException {
        return fory.serialize(obj);
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
            return fory.deserialize(data);
        } else {
            if (toType instanceof Class) {
                //处理匿名名类
                Class<?> toClz = (Class<?>) toType;
                if (toClz.isAnonymousClass()) {
                    toType = toClz.getGenericSuperclass();
                }
            }

            Class<?> clz = ClassUtil.getTypeClass(toType);
            return fory.deserialize(data, clz);
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
        return fory.deserialize(ctx.bodyAsBytes());
    }
}