package org.noear.solon.ai.chat.dialect;

import org.noear.solon.ai.chat.ChatConfig;
import org.noear.solon.ai.chat.ChatResponseAmend;

/**
 * DashScope 方言（阿里云产品）
 *
 * @author noear
 * @since 3.1
 */
public class DashscopeDialect extends AbstractDialect {
    @Override
    public boolean matched(ChatConfig config) {
        if (config.provider() != null) {
            return "dashscope".equals(config.provider());
        } else {
            return false;
        }
    }

    @Override
    public boolean parseResponseJson(ChatConfig config, ChatResponseAmend resp, String json) {
        return false;
    }
}
