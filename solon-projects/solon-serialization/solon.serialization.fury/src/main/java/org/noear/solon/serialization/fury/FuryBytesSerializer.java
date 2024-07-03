package org.noear.solon.serialization.fury;

import org.apache.fury.Fury;
import org.apache.fury.ThreadLocalFury;
import org.apache.fury.ThreadSafeFury;
import org.apache.fury.config.Language;
import org.apache.fury.resolver.AllowListChecker;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.util.ClassUtil;
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

    @Override
    public String getContentType() {
        return label;
    }

    @Override
    public boolean matched(Context ctx, String mime) {
        if (mime == null) {
            return false;
        } else {
            return mime.startsWith(label);
        }
    }

    @Override
    public String name() {
        return "fury-bytes";
    }

    @Override
    public byte[] serialize(Object obj) throws IOException {
        return fury.serialize(obj);
    }

    @Override
    public Object deserialize(byte[] data, Type toType) throws IOException {
        if (toType == null) {
            return fury.deserialize(data);
        } else {
            Class<?> clz = ClassUtil.getTypeClass(toType);
            return fury.deserializeJavaObject(data, clz);
        }
    }

    @Override
    public void serializeToBody(Context ctx, Object data) throws IOException {
        ctx.contentType(getContentType());

        if (data instanceof ModelAndView) {
            ctx.output(serialize(((ModelAndView) data).model()));
        } else {
            ctx.output(serialize(data));
        }
    }

    @Override
    public Object deserializeFromBody(Context ctx) throws IOException {
        return fury.deserialize(ctx.bodyAsBytes());
    }
}
