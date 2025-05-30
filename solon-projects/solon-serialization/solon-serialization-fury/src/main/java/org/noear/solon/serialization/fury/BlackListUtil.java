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

import org.noear.solon.core.util.ResourceUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 黑名单工具
 *
 * @author noear
 * @since 2.8
 */
public class BlackListUtil {
    private static final String BLACKLIST_TXT_PATH = "META-INF/solon/furyBlackList.txt";
    private static Collection<String> blackList;

    /**
     * 获取黑名单
     */
    public static Collection<String> getBlackList() {
        if (blackList == null) {
            try (InputStream is = ResourceUtil.getResourceAsStream(BLACKLIST_TXT_PATH)) {
                if (is != null) {
                    blackList = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                            .lines()
                            .collect(Collectors.toSet());
                } else {
                    throw new IllegalStateException(String.format("Read blacklist %s failed", BLACKLIST_TXT_PATH));
                }
            } catch (IOException e) {
                throw new IllegalStateException(String.format("Read blacklist %s failed", BLACKLIST_TXT_PATH), e);
            }
        }

        return blackList;
    }
}