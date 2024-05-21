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