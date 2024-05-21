package org.noear.solon.serialization.fury;

import io.fury.Fury;
import io.fury.ThreadLocalFury;
import io.fury.ThreadSafeFury;
import io.fury.config.Language;
import io.fury.resolver.AllowListChecker;
import org.noear.solon.serialization.Serializer;

import java.io.IOException;
import java.util.Collection;

/**
 * Fury 字节序列化
 *
 * @author noear
 * @since 2.8
 */
public class FuryBytesSerializer implements Serializer<byte[]> {
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
    public byte[] serialize(Object obj) throws IOException {
        return fury.serialize(obj);
    }

    @Override
    public Object deserialize(byte[] data, Class<?> clz) throws IOException {
        if (clz == null) {
            return fury.deserialize(data);
        } else {
            return fury.deserializeJavaObject(data, clz);
        }
    }
}
