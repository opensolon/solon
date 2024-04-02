package org.noear.solon.serialization.fury;

import io.fury.Fury;
import io.fury.ThreadLocalFury;
import io.fury.ThreadSafeFury;
import io.fury.config.Language;
import io.fury.resolver.AllowListChecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author noear
 * @since 2.5
 */
public class FuryUtil {
    private static final String BLACKLIST_TXT_PATH = "solon/furyBlackList.txt";
    public static Set<String> DEFAULT_BLACKLIST_SET ;

    public static final ThreadSafeFury fury = getFurySerializer();

    private static ThreadSafeFury getFurySerializer() {
        loadBlackList();
        return new ThreadLocalFury(classLoader -> {
            Fury f = Fury.builder()
                    .withAsyncCompilation(true)
                    .withLanguage(Language.JAVA)
                    .withRefTracking(true)
                    .requireClassRegistration(false)
                    .build();

            // set custom blacklist
            AllowListChecker blackListChecker = new AllowListChecker(AllowListChecker.CheckLevel.WARN);
            Set<String> blackList = DEFAULT_BLACKLIST_SET;
            // To setting checker
            f.getClassResolver().setClassChecker(blackListChecker);
            blackListChecker.addListener(f.getClassResolver());
            // BlackList classes use wildcards
            for (String key : blackList) {
                blackListChecker.disallowClass(key + "*");
            }
            return f;
        });
    }

    private static void loadBlackList(){
        try (InputStream is =
                     FuryUtil.class.getClassLoader().getResourceAsStream(BLACKLIST_TXT_PATH)) {
            if (is != null) {
                DEFAULT_BLACKLIST_SET =
                        new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                                .lines()
                                .collect(Collectors.toSet());
            } else {
                throw new IllegalStateException(
                        String.format("Read blacklist %s failed", BLACKLIST_TXT_PATH));
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    String.format("Read blacklist %s failed", BLACKLIST_TXT_PATH), e);
        }
    }
}
