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
package org.noear.nami.coder.fury;

import org.apache.fury.Fury;
import org.apache.fury.ThreadLocalFury;
import org.apache.fury.ThreadSafeFury;
import org.apache.fury.config.Language;
import org.apache.fury.resolver.AllowListChecker;
import org.noear.solon.core.util.ResourceUtil;

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
    private static final String BLACKLIST_TXT_PATH = "META-INF/nami/furyBlackList.txt";
    public static Set<String> DEFAULT_BLACKLIST_SET;

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

    private static void loadBlackList() {
        try (InputStream is = ResourceUtil.getResourceAsStream(BLACKLIST_TXT_PATH)) {
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