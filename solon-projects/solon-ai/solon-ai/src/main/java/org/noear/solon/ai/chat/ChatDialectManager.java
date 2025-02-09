package org.noear.solon.ai.chat;

import org.noear.solon.ai.chat.dialect.OllamaDialect;
import org.noear.solon.ai.chat.dialect.OpenaiDialect;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2025/2/9 created
 */
public class ChatDialectManager {
    private static Map<String, ChatDialect> dialects = new HashMap<>();

    static {
        register(OllamaDialect.getInstance());
        register(OpenaiDialect.instance());
    }

    public static ChatDialect get(String provider) {
        if (provider == null) {
            return OpenaiDialect.instance();
        } else {
            return dialects.getOrDefault(provider, OpenaiDialect.instance());
        }
    }

    public static void register(ChatDialect dialect) {
        dialects.put(dialect.provider(), dialect);
    }
}
